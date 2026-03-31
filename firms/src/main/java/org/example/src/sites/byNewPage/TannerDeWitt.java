package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TannerDeWitt extends ByNewPage {
    private final By[] byRoleArray = {
            By.tagName("span"),
    };

    public TannerDeWitt() {
        super(
            "Tanner DeWitt",
            "https://www.tannerdewitt.com/our-people/",
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(500);
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("ul.people-list > li")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("page-title")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("lawyer-title")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        WebElement div = lawyer.findElement(By.className("section-lawyer"));
        String[] socials = super.getSocialsFromText(div.getText());
        String email = ""; String phone = "";

        email = driver.findElement(By.id("profile-email")).getAttribute("href");

        return new String[]{ email, socials[1] };
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.id("content"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", link,
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Hong Kong",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}

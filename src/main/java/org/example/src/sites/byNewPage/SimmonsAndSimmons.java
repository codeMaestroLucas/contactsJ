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

public class SimmonsAndSimmons extends ByNewPage {
    private final By[] byRoleArray = {
            By.cssSelector("p.contact-card_text")
    };

    public SimmonsAndSimmons() {
        super(
            "Simmons And Simmons",
            "https://www.simmons-simmons.com/en/people?query=&filters=false&type=all&sectors=&services=&office=#noScroll",
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.rollDown(5, 0.4); // 258 rolls, 3 lawyers for roll
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.contacts-grid > a[href^='/en/people/']")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("//*[@id=\"content\"]/div/section[1]/div/div[2]/h1")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("//*[@id=\"content\"]/div/section[1]/div/div[2]/p")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("//*[@id=\"content\"]/div/section[1]/div/div[2]/a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
    }


    private String[] getSocials() {
        String email = ""; String phone = "";

        WebElement socialsDiv = driver.findElement(By.cssSelector("aside"));
        email = socialsDiv.findElement(By.cssSelector("a[href^='mailto']")).getAttribute("href");
        phone = socialsDiv.findElement(By.cssSelector("div")).getText();

        return new String[]{ email, phone };
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.cssSelector("body"));

        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}

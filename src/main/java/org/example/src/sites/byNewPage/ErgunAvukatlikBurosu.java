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

public class ErgunAvukatlikBurosu extends ByNewPage {

    private final By[] byRoleArray = {
            By.cssSelector(".text span")
    };

    public ErgunAvukatlikBurosu() {
        super(
                "Ergün Avukatlik Bürosu",
                "https://www.cergun.av.tr/people/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[href*='/calisanlar/']")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h2")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("span[style*='color: #800000']")};
        String role = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        return role.split(",")[0].trim();
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> detailSpans = lawyer.findElements(By.cssSelector("span[style*='float: left']"));
            String phone = detailSpans.get(1).getText().trim();
            String email = detailSpans.get(3).getText().trim();
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement content = driver.findElement(By.xpath("/html/body/section/div/div/div[3]/div/div/div[2]"));

        String[] socials = this.getSocials(content);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(content),
                "role", this.getRole(content),
                "firm", this.name,
                "country", "Turkey",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "902122809091" : socials[1]
        );
    }
}

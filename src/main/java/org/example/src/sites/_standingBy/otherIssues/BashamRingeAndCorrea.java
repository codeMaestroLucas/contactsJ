package org.example.src.sites._standingBy.otherIssues;

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

public class BashamRingeAndCorrea extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("jet-listing-dynamic-terms"),
            By.cssSelector("span.jet-listing-dynamic-terms__link")
    };

    public BashamRingeAndCorrea() {
        super(
            "Basham Ringe And Correa",
            "https://basham.com.mx/en/our-team/",
            10,
            1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) return;

        // Fail to click on the next page
        WebElement pagination = driver.findElement(By.className("jet-filters-pagination"));
        MyDriver.clickOnElement(
                pagination.findElement(By.cssSelector("div[data-value='" + (index + 1) + "']"))
        );

    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("e-con-inner")
                    )
            );
//            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
            return  null;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h2 > a")
        };
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h1")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "outerHTML", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("div.elementor-element:last-child"),
                By.cssSelector("span.jet-listing-dynamic-terms__link:last-child")
        };
         return extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "outerHTML", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("span.jet-listing-dynamic-terms__link")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE AREA", "outerHTML", LawyerExceptions::countryException);
    }

    private String[] getSocials() {
        try {
            List<WebElement> socials = driver
                    .findElement(By.xpath("/html/body/header/section/div/div[2]/div"))
                    .findElements(By.cssSelector("div.jet-listing-dynamic-field__content"));
            return super.getSocials(socials, true);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.xpath("/html/body/div[1]/section[1]/div/div/div"));

        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Mexico",
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}

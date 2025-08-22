package org.example.src.sites.byNewPage;

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

public class CovenantChambers extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("list-item-content__description"),
            By.cssSelector("p")
    };


    public CovenantChambers() {
        super(
            "Covenant Chambers",
            "https://www.covenantchambers.com/firm",
            1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.className("sqs-cookie-banner-v2-accept"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "director",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("list-item-content")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("list-item-content__button")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        MyDriver.openNewTab(element.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h1")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h2")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getPracticeArea(WebElement lawyer) {
        try {
            List<WebElement> pElements = lawyer.findElements(By.cssSelector("p"));
            return pElements.get(3).getText();
        } catch (Exception e) {
            return "";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        String email = ""; String phone = "";

        email = lawyer.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href");
        List<WebElement> pElements = lawyer.findElements(By.cssSelector("p"));
        for (WebElement pElement : pElements) {

            phone = pElement.getText().replaceAll("\\D", "");
            if (phone.length() > 5) break;
        }

        return new String[]{ email, phone };
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement socialDiv = null;
        WebElement infoDiv = null;

        try {
            socialDiv = driver.findElement(By.className("sqs-col-4"));
            infoDiv = driver.findElement(By.className("sqs-col-7"));
        // It changes the HTML
        } catch (Exception e) {
            WebElement alternativeDiv = driver.findElement(By.className("content"));
            socialDiv = alternativeDiv;
            infoDiv = alternativeDiv;
        }

        String[] socials = this.getSocials(socialDiv);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(infoDiv),
            "role", this.getRole(infoDiv),
            "firm", this.name,
            "country", "Singapore",
            "practice_area", this.getPracticeArea(socialDiv),
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "6566358885" : socials[1]
        );
    }
}

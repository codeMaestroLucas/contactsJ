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

public class Legance extends ByNewPage {
    private final By[] byRoleArray = {
            By.cssSelector("h5")
    };

    public Legance() {
        super(
            "Legance",
            "https://www.legance.com/professionals/",
            52
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.legance.com/professionals/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        MyDriver.clickOnAddBtn(By.className("iubenda-cs-accept-btn"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "managing associate",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("professional-item")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("professional-banner-header"),
                By.className("entry-title")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME",  "textContent",LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("professional-banner-header"),
                By.className("text-uppercase"),
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "ROLE",  "textContent",LawyerExceptions::roleException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("professional-banner-header"),
                By.cssSelector("a[href^='https://www.legance.com/office/']")
        };
        String country = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY",  "textContent",LawyerExceptions::countryException);
        return country.toLowerCase().contains("london") ? "England" : "Italy";
    }

    private String getPracticeArea() throws LawyerExceptions {
        WebElement lawyer = driver
                .findElement(By.cssSelector("a.text-decoration-none[href^='https://www.legance.com/practice-areas/']"));
        By[] byArray = new By[]{
                By.className("font-weight-normal")
        };
           return extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE AREA",  "textContent",LawyerExceptions::practiceAreaException);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.xpath("/html/body/div/div/main/div/article/header/div/div[1]"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", this.getPracticeArea(),
                "email", socials[0],
                "phone", "39028963071"
        );
    }
}

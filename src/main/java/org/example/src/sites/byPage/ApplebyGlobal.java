package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ApplebyGlobal extends ByPage {
    public ApplebyGlobal() {
        super(
                "Appleby Global",
                "httpa://www.applebyglobal.com/people/page/1/",
                9,
                3
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.applebyglobal.com/people/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        if (index <= 0) {
            MyDriver.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{
                By.cssSelector(".u-font-size-12.u-font-weight-normal.u-uppercase.u-letter-spacing-supersmall.u-margin-bottom-10")
        };
        String[] validRoles = new String[]{"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("grid-item__content")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a.u-decoration-none")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a.u-decoration-none")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector(".u-font-size-12.u-font-weight-normal.u-uppercase.u-letter-spacing-supersmall.u-margin-bottom-10")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("u-margin-bottom-0")
        };
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);

        if (office.trim().equalsIgnoreCase("bvi")) {
            return "the British Virgin Islands";
        }
        return office;
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("p.u-font-size-14.u-line-height-22 > a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
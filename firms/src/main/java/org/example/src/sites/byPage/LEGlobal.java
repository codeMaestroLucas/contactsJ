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
import java.util.Objects;

public class LEGlobal extends ByPage {
    private final By[] byRoleArray = {
            By.className("people-position")
    };

    public LEGlobal() {
        super(
                "L&E Global",
                "https://leglobal.law/about-l-e-global/people/",
                9,
                3
        );
    }

    @Override
    protected void accessPage(int index) {
        String otherUrl = "https://leglobal.law/about-l-e-global/people/?_paged=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "principal", "director", "chair", "senior associate", "managing associate"};
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
        List<WebElement> lawyers = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("contact-wrapper"))
        );
        return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
    }

    private String getLink(WebElement lawyer) {
        return this.link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h2")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }
    private String getFirm(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("top-title")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) {
        try {
            String addressBlock = lawyer.findElement(By.cssSelector("dd:nth-of-type(3)")).getAttribute("innerHTML");
            String[] lines = addressBlock.split("<br>");
            // The country is usually the line before the phone number
            String country = lines[lines.length - 4];

            if (country.toLowerCase().contains("United States")) return "USA";

            return country;
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("dl.data-list a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.getFirm(lawyer).trim(),
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
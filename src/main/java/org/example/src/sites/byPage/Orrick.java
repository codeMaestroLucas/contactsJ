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

public class Orrick extends ByPage {
    private final By[] byRoleArray = {
            By.tagName("small")
    };

    public Orrick() {
        super(
                "Orrick",
                "",
                7,
                3
        );
    }

    private String currentCountry = "";

    private final String[] otherLinks = {
            "Düsseldorf",
            "Geneva",
            "London",
            "Milan",
            "Paris",

            "Munich",
            "Paris+Tech+Studio",
            "Rome",
//            "",
    };


    private String setIndexAndCountry(int index) {
        switch (index) {
            case 0, 5:
                currentCountry = "Germany";
                break;

            case 1:
                currentCountry = "Switzerland";
                break;

            case 2:
                currentCountry = "England";
                break;

            case 3, 7:
                currentCountry = "Italy";
                break;

            case 4, 6:
                currentCountry = "France";
                break;

            default:
                currentCountry = "Unknown";
                break;
        }

        return "https://www.orrick.com/en/People?o=" + otherLinks[index] + "&t=personnel";
    }

    protected void accessPage(int index) {
        this.driver.get(setIndexAndCountry(index));
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "managing associate", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("article-wrapper")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3 > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3 > a")};
        String nameAndRole = extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "innerHTML", LawyerExceptions::nameException);
        return nameAndRole.split("<small>")[0];
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String roleAndPA = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
        return roleAndPA.split(",")[0];
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("ul > li > a"));
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
                "country",  currentCountry,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}
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

public class Sidley extends ByPage {
    private final By[] byRoleArray = {
            By.className("contact-level")
    };

    public Sidley() {
        super(
                "Sidley",
                "",
                4,
                2
        );
    }

    private String currentCountry = "";

    // more urls of countris to avoid for now
    private final String[] otherLinks = {
            "1b06f8d8-2bec-45ce-bb89-1c1aef6a9ffb&skip=20&currentviewid=83e3dcaa-1264-4226-8ee6-380c20e95bea&reload=false&scroll=4085",
            "5047e466-5b7d-4483-a028-1636e4af5085&currentviewid=83e3dcaa-1264-4226-8ee6-380c20e95bea&reload=false&scroll=182",
            "6f3b97d3-53f3-4df3-8617-03422f6ef9c6&skip=180&currentviewid=83e3dcaa-1264-4226-8ee6-380c20e95bea&reload=false&scroll=31714",
            "332a6b5a-66e4-473c-ae4d-6ded775b107a&skip=20&currentviewid=83e3dcaa-1264-4226-8ee6-380c20e95bea&reload=false&scroll=3928",
//            "",
    };


    private String setIndexAndCountry(int index) {
        switch (index) {
            case 0:
                currentCountry = "Belgium";
                break;

            case 1:
                currentCountry = "Switzerland";
                break;

            case 2:
                currentCountry = "England";
                break;

            case 3:
                currentCountry = "Germany";
                break;

            default:
                currentCountry = "Unknown";
                break;
        }

        return "https://www.sidley.com/en/global/people/?offices=" + otherLinks[index];
    }

    protected void accessPage(int index) {
        this.driver.get(setIndexAndCountry(index));
        MyDriver.waitForPageToLoad();

        try {
            MyDriver.clickOnElementMultipleTimes(By.className("btn-read-more"), 5, 0.5);
        } catch (Exception e) {}
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "advisor", "managing associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("contact-card")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("contact-name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.className("contact-email")).getAttribute("href");
            phone = lawyer.findElement(By.className("contact-tel")).getAttribute("href");
        } catch (Exception e) {
            // Social not found
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", currentCountry,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
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

public class HollandAndKnight extends ByPage {
    private String currentCountry = "";

    String[] otherLinkds = {
            "https://www.hklaw.com/en/professionals?office=02471e5d-8fa5-48b1-88f9-66573bace812", // Algeria
            "https://www.hklaw.com/en/professionals?office=fe84dd47-9b61-4b56-8045-eba9bd739a8d", // London
            "https://www.hklaw.com/en/professionals?office=95e5cd60-a2ff-4bb5-9e32-c066c70c07bb", // Mexico
            "https://www.hklaw.com/en/professionals?office=0dd8c219-e87b-4af8-bd0c-ba2cbd4205bd", // Mexico
    };

    private final By[] byRoleArray = {
            By.className("people-card__title")
    };

    public HollandAndKnight() {
        super(
                "Holland And Knight",
                "",
                4,
                2
        );
    }

    protected void accessPage(int index) {
        this.driver.get(otherLinkds[index]);
        MyDriver.waitForPageToLoad();

        switch (index) {
            case 0:
                currentCountry = "Algeria";
                break;
            case 1:
                currentCountry = "England";
                break;
            case 2, 3:
                currentCountry = "Mexico";
        }
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "advisor", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("people-card__card")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("people-card__name")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("people-card__name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
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
                "country", currentCountry,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
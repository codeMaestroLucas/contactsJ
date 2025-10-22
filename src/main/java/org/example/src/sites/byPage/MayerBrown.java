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

public class MayerBrown extends ByPage {
    private final By[] byRoleArray = {
            By.className("PeopleResults_card-title__wM4Hj")
    };



    public MayerBrown() {
        super(
                "Mayer Brown",
                "",
                5,
                3
        );
    }

    private String currentCountry = "";

    private final String[] otherLinks = {
            "Brussels",
            "Frankfurt",
            "London",
            "Paris",
            "Düsseldorf",

//            "Brasília%20(T%26C)",
//            "Dubai",
//            "Hong%20Kong",
//            "Rio%20de%20Janeiro%20(T%26C)",
//            "São%20Paulo%20(T%26C)",
//            "Singapore",
//            "Tokyo",
//            "Vitória%20(T%26C)",
    };


    private String setIndexAndCountry(int index) {
        switch (index) {
            case 0:
                currentCountry = "Belgium";
                break;
            case 1, 4:
                currentCountry = "Germany";
                break;
            case 2:
                currentCountry = "England";
                break;
            case 3:
                currentCountry = "Paris";
                break;
            case 5, 8, 9, 12:
                currentCountry = "Brazil";
                break;
            case 6:
                currentCountry = "the UAE";
                break;
            case 7:
                currentCountry = "Hong Kong";
                break;
            case 10:
                currentCountry = "Singapore";
                break;
            case 11:
                currentCountry = "Japan";
                break;
            default:
                currentCountry = "Unknown";
                break;
        }

        return "https://www.mayerbrown.com/en/people?sortCriteria=%40alphasort%20ascending&f-officestitles=" + otherLinks[index];
    }

    protected void accessPage(int index) {
        this.driver.get(setIndexAndCountry(index));
        MyDriver.waitForPageToLoad();

        try {
            MyDriver.clickOnElementMultipleTimes(By.xpath("//*[@id=\"main\"]/div[2]/div[2]/button"), 4, 0.5);
        } catch (Exception e) {}
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("PeopleResults_card__Oy_u5")
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
        By[] byArray = {By.className("PeopleResults_card-name__f7wLL")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.className("PeopleResults_card-email__nu1pw")).getAttribute("href");
            phone = lawyer.findElement(By.className("PeopleResults_card-phone__J3cLX")).getAttribute("href");
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
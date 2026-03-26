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

public class HBNLaw extends ByPage {

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "curacao", "Curaçao",
            "aruba", "Aruba",
            "sint-maarten", "Sint Maarten",
            "netherlands", "the Netherlands",
            "suriname", "Suriname"
    );


    private final By[] byRoleArray = {
            By.cssSelector("div")
    };

    public HBNLaw() {
        super(
                "HBN Law",
                "https://hbnlawtax.com/people/",
                1,
                2
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
        List<WebElement> lawyers = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("person__content"))
        );
        return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='https://hbnlawtax.com/people/']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3.person__name > span")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String text = lawyer.getAttribute("innerHTML");
        String country = text.split("<br>")[1].trim();
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "the Netherlands");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a.link"));
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
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", ""
        );
    }
}
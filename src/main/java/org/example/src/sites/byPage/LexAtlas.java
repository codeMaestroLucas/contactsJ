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

public class LexAtlas extends ByPage {

    private final By[] byRoleArray = {
            By.tagName("h4")
    };

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "el-salvador", "El Salvador",
            "guatemala", "Guatemala",
            "honduras-san-pedro-sula", "Honduras",
            "honduras-tegucigalpa", "Honduras"
    );

    public LexAtlas() {
        super(
                "Lex Atlas",
                "https://lexatlas.com/en/team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".carta")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='https://lexatlas.com/en/']")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        return link.isEmpty() ? this.link : link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h3")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, byRoleArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, country);
    }

    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h5")};
        return extractor.extractLawyerText(lawyer, byArray, "EMAIL", LawyerExceptions::emailException);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", this.getEmail(lawyer),
                "phone", ""
        );
    }
}

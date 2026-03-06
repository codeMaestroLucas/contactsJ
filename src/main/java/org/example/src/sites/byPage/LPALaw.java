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

import static java.util.Map.entry;

public class LPALaw extends ByPage {

    private final By[] byRoleArray = {
            By.className("blocphoto__content__fonction")
    };

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("algiers", "Algeria"),
            entry("casablanca", "Morocco"),
            entry("douala/yaoundé", "Cameroon"),
            entry("dubai", "the UAE"),
            entry("frankfurt", "Germany"),
            entry("hamburg", "Germany"),
            entry("hanoi", "Vietnam"),
            entry("ho chi minh city", "Vietnam"),
            entry("hong kong", "China"),
            entry("munich", "Germany"),
            entry("paris", "France"),
            entry("shanghai", "China"),
            entry("singapore", "Singapore"),
            entry("tokyo", "Japan"),
            entry("vienna", "Austria")
    );

    public LPALaw() {
        super(
                "LPA Law Firm",
                "https://www.lpalaw.com/en/team/#associe",
                36
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.lpalaw.com/en/team/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.lesavocats > article")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        return lawyer.findElement(By.cssSelector("a[href*='https://www.lpalaw.com/en/team/']")).getAttribute("href");
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("blocphoto__content__nom")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("blocphoto__content__lieu")};
        String country = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "textContent", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, country);
    }

    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("blocphoto__content__mail")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "EMAIL", "textContent", LawyerExceptions::emailException);
    }

    private String getPhone(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("blocphoto__content__tel")};
        String phone = extractor.extractLawyerAttribute(lawyer, byArray, "PHONE", "textContent", LawyerExceptions::phoneException);
        return phone.replace("T : ", "").trim();
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
                "phone", this.getPhone(lawyer).isEmpty() ? "xxxxxx" : this.getPhone(lawyer)
        );
    }
}

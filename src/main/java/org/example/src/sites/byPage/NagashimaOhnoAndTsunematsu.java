package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class NagashimaOhnoAndTsunematsu extends ByPage {
    private final By[] byRoleArray = {
            By.className("position")
    };

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("Tokyo", "Japan"),
            entry("New York", "USA"),
            entry("Shanghai", "China"),
            entry("Singapore", "Singapore"),
            entry("Bangkok", "Thailand"),
            entry("Ho Chi Minh City", "Vietnam"),
            entry("Hanoi", "Vietnam"),
            entry("Jakarta", "Indonesia"),
            entry("London", "England")
    );

    public NagashimaOhnoAndTsunematsu() {
        super(
                "Nagashima Ohno And Tsunematsu",
                "https://www.nagashima.com/en/lawyers/",
                27,
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.nagashima.com/en/lawyers/page/"+ (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{ "partner", "counsel", "advisor", "adviser", "senior associate" };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("lawyers-card")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.tagName("a") };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.className("name") };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        // Seletor corrigido conforme <p class="office">
        By[] byArray = { By.className("office") };
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "Japan");
    }

    private String constructEmail(String name) {
        String[] nameParts = TreatLawyerParams.treatNameForEmail(name).split(" ");
        if (nameParts.length < 2) return nameParts[0].toLowerCase() + "@noandt.com";
        String firstName = nameParts[0].toLowerCase();
        String lastName = nameParts[nameParts.length - 1].toLowerCase();
        return firstName + "_" + lastName + "@noandt.com";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", this.constructEmail(name),
                "phone", "81368897000"
        );
    }
}

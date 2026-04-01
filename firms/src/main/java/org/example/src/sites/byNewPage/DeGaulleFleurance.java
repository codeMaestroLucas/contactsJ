package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class DeGaulleFleurance extends ByNewPage {

    public DeGaulleFleurance() {
        super(
                "De Gaulle Fleurance",
                "https://www.degaullefleurance.com/en/annuaire/",
                6
        );
    }
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "33", "France",
            "41", "Switzerland",
            "32", "Belgium",
            "971", "the UAE",
            "225", "Ivory Coast",
            "221", "Senegal"
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.degaullefleurance.com/en/annuaire/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] byRoleArray = {By.className("fonction")};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("link")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        By[] byName = {By.className("titre")};
        By[] byRole = {By.className("fonction")};

        String name = extractor.extractLawyerText(lawyer, byName, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, byRole, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        String[] socials = super.getSocials(driver.findElements(By.cssSelector(".details a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", this.getCountry(socials[1]),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "33184753451" : socials[1]
        );
    }

    private String getCountry(String phone) {
        return this.siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, phone, "France");
    }
}

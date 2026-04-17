package org.example.src.sites.mundial;

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
import java.util.Objects;

import static java.util.Map.entry;

public class Cooley extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("beijing", "China"),
            entry("brussels", "Belgium"),
            entry("hong kong", "China"),
            entry("london", "England"),
            entry("shanghai", "China"),
            entry("singapore", "Singapore")
    );

    public Cooley() {
        super(
                "Cooley",
                "https://www.cooley.com/people#t=cooley-coveo-tab-people-listing&sort=%40personsortname%20ascending&layout=card&f:cooley-offices-facet=[Beijing,Hong%20Kong,London,Seattle,Brussels,Shanghai,Singapore]",
                5,
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.cooley.com/people#first=" + (index * 15) + "&t=cooley-coveo-tab-people-listing&sort=%40personsortname%20ascending&layout=card&f:cooley-offices-facet=[Beijing,Brussels,Hong%20Kong,London,Seattle,Shanghai,Singapore]";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(2000);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article.teaser")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("teaser-position")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("CoveoResultLink")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div[data-field='@personprimaryoffice']")}, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String country = getCountry(lawyer);
        this.openNewTab(lawyer);
        WebElement hero = driver.findElement(By.className("hero-person"));

        String name = extractor.extractLawyerText(hero, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(hero, new By[]{By.className("eyebrow")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerAttribute(hero, new By[]{By.cssSelector("a[href*='mailto:']")}, "EMAIL", "textContent", LawyerExceptions::emailException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", email,
                "phone", "xxxxxx"
        );
    }
}

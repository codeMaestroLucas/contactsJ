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
import java.util.Objects;

import static java.util.Map.entry;

public class DSNavarroCastex extends ByNewPage {

    public DSNavarroCastex() {
        super(
                "DS Navarro Castex",
                "https://www.dsavocats.com/en/team/",
                1,
                2
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("montevideo", "Uruguay"),
            entry("barcelona", "Spain"),
            entry("brussels", "Belgium"),
            entry("buenos-aires", "Argentina"),
            entry("buenos aires", "Argentina"),
            entry("calgary", "Canada"),
            entry("casablanca", "Morocco"),
            entry("dakar", "Senegal"),
            entry("ho-chi-minh-city", "Vietnam"),
            entry("madrid", "Spain"),
            entry("milan", "Italy"),
            entry("montreal", "Canada"),
            entry("ottawa", "Canada"),
            entry("beijing", "China"),
            entry("quebec", "Canada"),
            entry("santiago", "Chile"),
            entry("shanghai", "China"),
            entry("singapore", "Singapore"),
            entry("stuttgart", "Germany"),
            entry("vancouver", "Canada")
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.equipe_items > div.grid > a")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("div.infos > p")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[] {By.cssSelector("p.bur")}, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "France");
    }
    
    
    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("span")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div.infos > p")}, "ROLE", LawyerExceptions::roleException);
        String country = getCountry(lawyer);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("details_profil"));

        String practice = extractor.extractLawyerText(container, new By[]{By.cssSelector("div.biographie P")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "541143126500" : socials[1]
        );
    }
}

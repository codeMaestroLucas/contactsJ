package org.example.src.sites.europe;

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

import static java.util.Map.entry;

public class ACTLEGAL extends ByNewPage {

    public ACTLEGAL() {
        super(
                "ACT LEGAL",
                "https://actlegal.com/professionals?page=3",
                1,
                3
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("austria", "Austria"),
            entry("belgium", "Belgium"),
            entry("bulgaria", "Bulgaria"),
            entry("czechia", "the Czech Republic"),
            entry("france", "France"),
            entry("germany", "Germany"),
            entry("hungary", "Hungary"),
            entry("italy", "Italy"),
            entry("netherlands", "the Netherlands"),
            entry("poland", "Poland"),
            entry("portugal", "Portugal"),
            entry("romania", "Romania"),
            entry("slovakia", "Slovakia"),
            entry("spain", "Spain"),
            entry("western balkans", "Western Balkans")
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.professional")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("p")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("professional-banner-content-info"));

        String name = extractor.extractLawyerText(container, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", this.getCountry(),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "420222537500" : socials[1]
        );
    }

    private String getCountry() {
        String country = driver.findElement(By.className("professional-contact")).getAttribute("innerHTML");
        String[] split = country.split("<br>");
        country = split[split.length - 1].replace("</p>", "");
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "----");
    }
}

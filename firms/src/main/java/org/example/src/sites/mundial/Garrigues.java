package org.example.src.sites.mundial;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Garrigues extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("bogota", "Colombia"),
            entry("brussels", "Belgium"),
            entry("casablanca", "Morocco"),
            entry("lima", "Peru"),
            entry("lisbon", "Portugal"),
            entry("london", "England"),
            entry("mexico city", "Mexico"),
            entry("monterrey", "Mexico"),
            entry("new york", "USA"),
            entry("oporto", "Portugal"),
            entry("queretaro", "Mexico"),
            entry("santiago de chile", "Chile"),
            entry("shanghai", "China"),
            entry("warsaw", "Poland")
    );

    public Garrigues() {
        super(
                "Garrigues",
                "https://www.garrigues.com/en_GB/team",
                151,
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.garrigues.com/en_GB/team?page=" + index;
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("li.list-team"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("div.col-xl-2:nth-child(2)")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".nombre-equipo a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }
    
    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[] {By.cssSelector("div.col-xl-2.enlace-team")}, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Spain");
    }
        

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("nombre-equipo")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div.col-xl-2:nth-child(2)")}, "ROLE", LawyerExceptions::roleException);
        String country = this.getCountry(lawyer);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("texto-detalle-equipo"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxx" : socials[1]
        );
    }
}

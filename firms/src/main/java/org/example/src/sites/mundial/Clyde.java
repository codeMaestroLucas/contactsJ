package org.example.src.sites.mundial;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Clyde extends ByNewPage {

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("aberdeen", "Scotland"),
            entry("abu dhabi", "the UAE"),
            entry("accra - associated office", "Ghana"),
            entry("bangkok", "Thailand"),
            entry("beijing", "China"),
            entry("belfast", "Northern Ireland"),
            entry("birmingham", "England"),
            entry("bogotá - associated office", "Colombia"),
            entry("brisbane", "Australia"),
            entry("bristol", "England"),
            entry("cairo", "Egypt"),
            entry("calgary", "Canada"),
            entry("cape town", "South Africa"),
            entry("caracas", "Venezuela"),
            entry("chongqing", "China"),
            entry("dar es salaam", "Tanzania"),
            entry("doha", "Qatar"),
            entry("dubai", "the UAE"),
            entry("dublin, harcourt centre", "Ireland"),
            entry("düsseldorf", "Germany"),
            entry("edinburgh, saltire court", "Scotland"),
            entry("glasgow", "Scotland"),
            entry("guildford", "England"),
            entry("hamburg", "Germany"),
            entry("hong kong", "China"),
            entry("johannesburg", "South Africa"),
            entry("jeddah", "Saudi Arabia"),
            entry("kuala lumpur - associated office", "Malaysia"),
            entry("kumasi - associated office", "Ghana"),
            entry("liverpool", "England"),
            entry("london, the st botolph building", "England"),
            entry("madrid", "Spain"),
            entry("manchester, 2 new bailey", "England"),
            entry("melbourne", "Australia"),
            entry("mexico city", "Mexico"),
            entry("milan", "Italy"),
            entry("montréal", "Canada"),
            entry("munich", "Germany"),
            entry("nairobi", "Kenya"),
            entry("new delhi - associated office", "India"),
            entry("newcastle", "England"),
            entry("paris", "France"),
            entry("perth", "Australia"),
            entry("rio de janeiro", "Brazil"),
            entry("riyadh", "Saudi Arabia"),
            entry("rotterdam", "the Netherlands"),
            entry("santiago", "Chile"),
            entry("sao paulo", "Brazil"),
            entry("shanghai", "China"),
            entry("singapore", "Singapore"),
            entry("southampton", "England"),
            entry("sydney", "Australia"),
            entry("toronto", "Canada"),
            entry("ulaanbaatar - associated office", "Mongolia"),
            entry("vancouver", "Canada"),
            entry("warsaw", "Poland")
    );

    public Clyde() {
        super(
                "Clyde",
                "https://www.clydeco.com/en/people/listing?persons=on&persons",
                84,
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.clydeco.com/en/people/listing?persons=on&search=&page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("div.people__tile"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("people__link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }
    
    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[] {By.cssSelector("a[href*='/en/locations/']")}, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }
    

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("people__name")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("person"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String country = getCountry(container);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}

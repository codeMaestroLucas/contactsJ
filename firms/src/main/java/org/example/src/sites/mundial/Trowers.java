package org.example.src.sites.mundial;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Trowers extends ByNewPage {

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("abu dhabi", "the UAE"),
            entry("bahrain", "Bahrain"),
            entry("dubai", "the UAE"),
            entry("malaysia", "Malaysia"),
            entry("oman", "Oman"),
            entry("saudi arabia", "Saudi Arabia"),
            entry("singapore", "Singapore")
    );
    
    public Trowers() {
        super(
                "Trowers",
                "https://www.trowers.com/people",
                1,
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

        // More than 30 rolls
        MyDriver.clickOnElementMultipleTimes(
                By.xpath("//*[@id=\"trowers\"]/main/section/div/div[3]/div/a"),
                10, 1
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.contact-card"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("contact-card__title")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }
    
    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerAttribute(lawyer, new By[] {By.className("contact-card__location")}, "COUNTRY", "textContent", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "England");
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("contact-card__name")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("contact-card__title")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String country = this.getCountry(lawyer);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("contact-card__caption"));

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

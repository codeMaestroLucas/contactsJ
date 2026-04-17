package org.example.src.sites.africa;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class ALN extends ByNewPage {

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("algeria", "Algeria"),
            entry("côte d’ivoire", "Ivory Coast"),
            entry("ethiopia", "Ethiopia"),
            entry("ghana", "Ghana"),
            entry("guinea", "Guinea"),
            entry("kenya", "Kenya"),
            entry("mauritius", "Mauritius"),
            entry("morocco", "Morocco"),
            entry("nigeria", "Nigeria"),
            entry("rwanda", "Rwanda"),
            entry("sudan", "Sudan"),
            entry("tanzania", "Tanzania"),
            entry("uae", "the UAE"),
            entry("uganda", "Uganda")
    );

    public ALN() {
        super(
                "ALN",
                "https://aln.africa/team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        // More than 30 rolls
        MyDriver.clickOnElementMultipleTimes(
                By.id("load-member"),
                5, 1
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".team-col"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("role")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }
    
    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerAttribute(lawyer, new By[] {By.className("role")}, "COUNTRY", "textContent", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Zambia");
    }
    

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h2")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("role")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("profile__information"));

        String country = getCountry(container);

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String phone = extractor.extractLawyerAttribute(container, new By[]{By.className("phone")}, "PHONE", "textContent", LawyerExceptions::socialsException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", phone.replace("Telephone:", "").trim()
        );
    }
}

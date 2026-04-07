package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PerezLlorca extends ByPage {

    public PerezLlorca() {
        super(
                "Pérez-Llorca",
                "https://www.perezllorca.com/en/team/",
                1
        );
    }

    public static final Map<String, String> DDD_TO_COUNTRY = Map.ofEntries(
            Map.entry("34", "Spain"),
            Map.entry("57", "Colombia"),
            Map.entry("32", "Belgium"),
            Map.entry("52", "Mexico"),
            Map.entry("351", "Portugal"),
            Map.entry("44", "United Kingdom"),
            Map.entry("1", "USA"),
            Map.entry("65", "Singapore")
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".cuadricula > div"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("h5")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h5")}, "ROLE", LawyerExceptions::roleException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h4"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);

        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);
        String country = this.getCountry(socials[1]);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34914360420" : socials[1]
        );
    }
    
    private String getCountry(String phone) {
        return this.siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, phone, "---");
    }
    
}

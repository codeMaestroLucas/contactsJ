package org.example.src.sites.mundial;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class PerezLlorca extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("bogota", "Colombia"),
            entry("brussels", "Belgium"),
            entry("ciudad-de-mexico", "Mexico"),
            entry("lisbon", "Portugal"),
            entry("londres", "England"),
            entry("medellin", "Colombia"),
            entry("monterrey", "Mexico"),
            entry("nueva-york", "USA"),
            entry("singapur", "Singapore")
    );

    public PerezLlorca() {
        super(
                "Pérez-Llorca",
                "https://www.perezllorca.com/en-mx/?post_type%5B%5D=abogado&search_type=abogados&s=&oficinas=Office&area-practica%5B%5D=Practices+and+Sectors&tipos=Position",
                1,
                3
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnAddBtn(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
        MyDriver.clickOnElementMultipleTimes(By.id("load-more-btn"), 10, 1);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            return MyDriver.wait.findElements(By.cssSelector("div.cuadricula div.item"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[] {By.className("oficinas")}, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Spain");
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "NAME", LawyerExceptions::nameException);
        String country = this.getCountry(lawyer);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.tagName("body"));

        String role = extractor.extractLawyerText(container, new By[]{By.cssSelector("div.titulo h2")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String[] socials = super.getSocialsFromText(container.findElement(By.className("contacto")).getAttribute("innerText"));

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("areas")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34914360425" : socials[1]
        );
    }
}
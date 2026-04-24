package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class LexQuire extends ByNewPage {

    public LexQuire() {
        super(
                "LexQuire",
                "https://lexquire.nl/mensen/",
                1
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "brussel", "Belgium",
            "dusseldorf", "Germany",
            "heerlen", "the Netherlands",
            "maastricht-airport", "the Netherlands",
            "mallorca-madrid-2", "Spain",
            "pamplona-3", "Spain",
            "poznan", "Poland",
            "sofia", "Bulgaria"
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("article.employees"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("introduction")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("w-grid-item-anchor")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    private String getCountry(String country) throws LawyerExceptions {
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "the Netherlands");
    }


    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("voornaam")}, "NAME", LawyerExceptions::nameException) + " " +
                     extractor.extractLawyerText(lawyer, new By[]{By.className("achternaam")}, "NAME", LawyerExceptions::nameException);

        String country = getCountry(lawyer.getAttribute("textContent"));
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("wpb_wrapper"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("introduction")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31433250903" : socials[1]
        );
    }
}

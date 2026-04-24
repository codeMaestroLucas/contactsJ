package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MannheimerSwartling extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public MannheimerSwartling() {
        super(
                "Mannheimer Swartling",
                "https://www.mannheimerswartling.se/en/our-people/",
                1
        );
    }

    public static final Map<String, String> DDD_TO_COUNTRY = Map.of(
            "32", "Belgium",
            "46", "Sweden",
            "1", "USA",
            "65", "Singapore"
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("entry-coworker-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.tagName("p")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }
    
    private String getCountry(String phone) {
        return this.siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, phone, "Sweden");
    }
    

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("entry-coworker-item_title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("coworker-contact-details-items"));

        String vcardHref = extractor.extractLawyerAttribute(container, new By[]{By.className("coworker-profile-vcard"), By.tagName("a")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] socials = vCard.getSocials(vcardHref);
        String country = this.getCountry(socials[2]);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "46859506000" : socials[1]
        );
    }
}

package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BorelBarbey extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public BorelBarbey() {
        super(
                "Borel & Barbey",
                "https://www.borel-barbey.ch/en/team/",
                1
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".oneTeamInLoopResultGrid"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".poste")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".avocat_name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".poste")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.cssSelector(".wpb_wrapper"));

        String vcardHref = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href*='vcard']")}, "VCARD", "href", LawyerExceptions::socialsException);
        
        String[] socials = vCard.getSocials(vcardHref);
        String email = socials[0];
        String phone = socials[1];

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "country", "Switzerland",
                "email", email,
                "phone", phone.isEmpty() ? "+41 22 707 18 00" : phone
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
    }
}

package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class BruchouFunesDeRioja extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public BruchouFunesDeRioja() {
        super(
                "Bruchou & Funes de Rioja",
                "https://bruchoufunes.com/en/profesionales/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("a.capsula-our-people"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("capsula-our-people--tag")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("our-people-details--net"));

        String role = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.className("fc-orange")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String vcardHref = extractor.extractLawyerAttribute(container, new By[]{By.className("js-generate-vcard")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] vcardData = vCard.getSocials(vcardHref);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Argentina",
                "practice_area", "",
                "email", socials[0].isEmpty() ? vcardData[0] : socials[0],
                "phone", socials[1].isEmpty() ? vcardData[1] : socials[1]
        );
    }
}

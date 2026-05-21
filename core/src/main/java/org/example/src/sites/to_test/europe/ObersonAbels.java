package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class ObersonAbels extends ByPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public ObersonAbels() {
        super(
                "Oberson Abels",
                "https://obersonabels.com/en/profiles",
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
        return MyDriver.wait.findElements(By.className("avocat_profile"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String vcardHref = extractor.extractLawyerAttribute(lawyer, new By[]{By.xpath(".//a[contains(text(), 'VCARD')]")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] socials = vCard.getSocials(vcardHref);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.xpath(".//a[contains(text(), 'PDF')]")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("avocat_name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("overlay-title")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Switzerland",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//h4[text()='SPECIALIZATIONS']/following-sibling::div")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "41223380000" : socials[1]
        );
    }
}

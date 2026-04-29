package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class HughesHughes extends ByPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public HughesHughes() {
        super(
                "Hughes & Hughes",
                "https://www.hughes.com.uy/our_people",
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
        return MyDriver.wait.findElements(By.className("listaPeople"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String vcardHref = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("contactVcard"), By.tagName("a")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] socials = vCard.getSocials(vcardHref);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("peopleNameLink")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("peopleNameLink")}, "NAME", LawyerExceptions::nameException),
                "role", "Partner",
                "firm", this.name,
                "country", "Uruguay",
                "practice_area", "",
                "email", socials[0].isEmpty() ? extractor.extractLawyerText(lawyer, new By[]{By.className("peopleMailLink")}, "EMAIL", LawyerExceptions::emailException) : socials[0],
                "phone", socials[1].isEmpty() ? "59829160988" : socials[1]
        );
    }
}

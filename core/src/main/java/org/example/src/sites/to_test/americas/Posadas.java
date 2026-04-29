package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Posadas extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public Posadas() {
        super(
                "Posadas",
                "https://www.ppv.com.uy/en/our-team/",
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
        return MyDriver.wait.findElements(By.cssSelector("div.thumb"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("contact"));

        String vcardHref = extractor.extractLawyerAttribute(driver.findElement(By.id("vcard")), new By[]{}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] socials = vCard.getSocials(vcardHref);

        return Map.of(
                "link", link,
                "name", name,
                "role", "",
                "firm", this.name,
                "country", "Uruguay",
                "practice_area", "",
                "email", socials[0].isEmpty() ? this.getSocials(container.findElements(By.className("item")), true)[0] : socials[0],
                "phone", socials[1].isEmpty() ? "59826041604" : socials[1]
        );
    }
}

package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Marval extends ByNewPage {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public Marval() {
        super(
                "Marval",
                "https://www.marval.com/profesionales?lang=en",
                ALPHABET.length()
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        driver.get(this.link);
        MyDriver.waitForPageToLoad();

        String targetLetter = String.valueOf(ALPHABET.charAt(index));

        WebElement alphabetContainer = driver.findElement(By.className("abecedario"));
        WebElement letterButton = alphabetContainer.findElement(By.id(targetLetter));

        MyDriver.clickOnElement(letterButton);
        Thread.sleep(1000);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.profesional-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("profesional-kind")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("profesional-name")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("profesional-kind")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("profesional-detail-datos"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Argentina",
                "practice_area", extractor.extractLawyerAttribute(container, new By[]{By.className("profesional-detail-area")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "541143100100" : socials[1]
        );
    }
}

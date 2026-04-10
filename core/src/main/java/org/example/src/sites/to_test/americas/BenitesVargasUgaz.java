package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BenitesVargasUgaz extends ByPage {

    public BenitesVargasUgaz() {
        super("Benites, Vargas & Ugaz", "https://bvu.pe/en/nuestro-equipo/", 1);
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> allLawyers = MyDriver.wait.findElements(By.cssSelector("div.dmach-grid-item"));
        List<WebElement> filteredLawyers = new ArrayList<>();

        for (WebElement lawyer : allLawyers) {
            try {
                String role = extractor.extractLawyerText(lawyer, new By[]{By.className("dmach-postmeta-value")}, "ROLE", (e) -> "");
                WebElement vCardBtn = lawyer.findElement(By.linkText("Download VCard"));

                if (siteUtl.isValidPosition(role, validRoles) && vCardBtn.isDisplayed()) {
                    filteredLawyers.add(lawyer);
                }
            } catch (Exception ignored) {}
        }
        return filteredLawyers;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String vCardUrl = extractor.extractLawyerAttribute(lawyer, new By[]{By.linkText("Download VCard")}, "VCARD", "href", LawyerExceptions::socialsException);
        VCard v = VCard.withDefaultPatterns();
        String[] socials = v.getSocials(vCardUrl);


        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("featured-image")}, "LINK", "src", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("dmach-post-title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("dmach-postmeta-value")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Peru",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "+5116159100" : socials[1]
        );
    }
}

package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Lener extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public Lener() {
        super(
                "Lener",
                "https://www.lener.es/en/professionals",
                5
        );
    }

    String[] vowels = {"a", "e", "i", "o", "u"};

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        }
        WebElement input = MyDriver.wait.findElement(By.id("query"));
        input.sendKeys(vowels[index]);
        input.sendKeys(Keys.ENTER);
        Thread.sleep(3000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.col-md-2.mb-5"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("card-subtitle")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("cover-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("titulo")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("card-subtitle")}, "ROLE", LawyerExceptions::roleException);
        String country = extractor.extractLawyerText(lawyer, new By[]{By.className("card-text")}, "COUNTRY", LawyerExceptions::countryException);

        String link = this.openNewTab(lawyer);

        String[] socials = {"", ""};
        try {
            String vcardUrl = MyDriver.wait.findElement(By.cssSelector("a[href*='vcard']")).getAttribute("href");
            socials = vCard.getSocials(vcardUrl);
        } catch (Exception e) {
            WebElement container = MyDriver.wait.findElement(By.className("mt-4"));
            socials = super.getSocials(container.findElements(By.tagName("p")), true);
        }

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34915774613" : socials[1]
        );
    }
}

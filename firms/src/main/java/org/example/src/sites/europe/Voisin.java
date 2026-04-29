package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Voisin extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public Voisin() {
        super(
                "Voisin",
                "https://www.voisinlaw.com/people",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("iso-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("job-title")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("name")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("job-title")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("info"));

        String vcardHref = container.findElement(By.cssSelector("a[href*='vcard']")).getAttribute("href");
        String[] socials = vCard.getSocials(driver, vcardHref);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Jersey",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "441534500300" : socials[1]
        );
    }
}

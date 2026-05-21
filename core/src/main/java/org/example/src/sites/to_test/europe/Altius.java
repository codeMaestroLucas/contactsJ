package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;

import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Altius extends ByPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public Altius() {
        super(
                "Altius",
                "https://www.altius.com/en/people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("altius-people-block-grid-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("p")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String vcardHref = lawyer.findElement(By.cssSelector("a[href$='.vcf']")).getAttribute("href");
        String[] socials = vCard.getSocials(vcardHref);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h5")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Belgium",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3227107811" : socials[1]
        );
    }
}

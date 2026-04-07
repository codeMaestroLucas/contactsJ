package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class ASPapadimitriouPartners extends ByNewPage {

    public ASPapadimitriouPartners() {
        super(
                "AS Papadimitriou & Partners",
                "https://saplegal.gr/people/",
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
        try {
            return MyDriver.wait.findElements(By.className("lawyer"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("lawyer-url")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("lawyer-title")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("lawyer-profile"));
        String role = extractor.extractLawyerText(container, new By[]{By.className("caption")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocialsFromText(container.findElement(By.className("lawyer-contact")).getText());
        socials[0] = container.findElement(By.cssSelector("a")).getAttribute("href");

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Greece",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "302109409960" : socials[1]
        );
    }
}

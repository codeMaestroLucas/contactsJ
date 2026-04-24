package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class KooijmanAutar extends ByPage {

    public KooijmanAutar() {
        super(
                "Kooijman Autar",
                "https://www.kooijmanautar.nl/our-people",
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
        String[] validRoles = {"notaris", "mediator", "notarisklerk"};
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.img-zoom-hover"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("p.text-secondary")}, true, validRoles);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h2")}, "NAME", "textContent", LawyerExceptions::nameException),
                "role", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("p.text-secondary")}, "ROLE", "textContent", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31102858888" : socials[1]
        );
    }
}

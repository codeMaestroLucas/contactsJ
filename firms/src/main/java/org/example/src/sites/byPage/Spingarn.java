package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Spingarn extends ByPage {

    public Spingarn() {
        super("Spingarn", "https://en.spingarn.ec/equipo", 1);
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.profesional-card.w-dyn-item"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("team-text-3")}, true, validRoles);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("team-text-2")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("team-text-3")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Ecuador",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "+59323332460" : socials[1]
        );
    }
}

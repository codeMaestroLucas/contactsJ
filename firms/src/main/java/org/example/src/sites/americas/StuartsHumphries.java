package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class StuartsHumphries extends ByPage {

    public StuartsHumphries() {
        super(
                "STUARTS HUMPHRIES",
                "https://www.stuartslaw.com/site/people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("li.card.visible"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("jobtitle")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".name a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("name")}, "NAME", "textContent", LawyerExceptions::nameException),
                "role", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("jobtitle")}, "ROLE", "textContent", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "the Cayman Islands",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "13459493344" : socials[1]
        );
    }
}

package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BignonLebray extends ByPage {

    public BignonLebray() {
        super(
                "Bignon Lebray",
                "https://www.bignonlebray.com/en/the-team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("pdo-personne"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("text-primary")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector("ul.social-list a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("stretched-link")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("text-primary")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "France",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "33144353700" : socials[1]
        );
    }
}

package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class FriedFrank extends ByPage {

    public FriedFrank() {
        super(
                "Fried Frank",
                "https://www.friedfrank.com/our-people?office=London",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        for (int i = 0; i < 15; i++) {
            MyDriver.scrollToBottom(1000);
            Thread.sleep(1000);
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.search__autofill-result"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("search__autofill-result-position")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("search__autofill-result-name"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("search__autofill-result-name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("search__autofill-result-position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "United Kingdom",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h5.search__autofill-result-other:first-of-type")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[1],
                "phone", socials[0].isEmpty() ? "442079726202" : socials[0]
        );
    }
}

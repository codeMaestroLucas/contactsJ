package org.example.src.sites.to_test.mundial;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class McDermottWillSchulte extends ByPage {

    public McDermottWillSchulte() {
        super(
                "McDermott Will & Schulte",
                "https://www.mcdermottlaw.com/people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("section-people-search-element"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("location")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String email = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[onclick*='setPeopleEmail']")}, "EMAIL", "onclick", LawyerExceptions::emailException)
                .replace("setPeopleEmail('", "").replace("')", "");

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("profile-title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("location")}, "ROLE", LawyerExceptions::roleException).split("\\|")[0].trim(),
                "firm", this.name,
                "country", "United States",
                "practice_area", "",
                "email", email,
                "phone", extractor.extractLawyerText(lawyer, new By[]{By.className("phone-number")}, "PHONE", LawyerExceptions::phoneException)
        );
    }
}

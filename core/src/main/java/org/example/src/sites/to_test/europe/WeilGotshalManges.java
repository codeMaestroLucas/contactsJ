package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class WeilGotshalManges extends ByPage {

    public WeilGotshalManges() {
        super(
                "Weil Gotshal & Manges",
                "https://www.weil.com/people",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("ppl-item"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("ppl-item-status"), By.tagName("b")}, true, validRoles);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("ppl-item-status"), By.tagName("b")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Germany",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.className("ppl-item-categories")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", "",
                "phone", extractor.extractLawyerText(lawyer, new By[]{By.className("ppl-item-tel")}, "PHONE", LawyerExceptions::phoneException)
        );
    }
}

package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class TragardhAdvokatbyra extends ByPage {

    public TragardhAdvokatbyra() {
        super(
                "Trägårdh Advokatbyrå AB",
                "https://www.tragardh.se/en/employee/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("employee-teaser"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("employee-teaser__title")}, true, validRoles);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "data-href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("employee-teaser__name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("employee-teaser__title")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Sweden",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.className("competence-terms")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "46406655617" : socials[1]
        );
    }
}

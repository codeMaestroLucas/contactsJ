package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Ontier extends ByNewPage {

    public Ontier() {
        super(
                "Ontier",
                "https://www.ontier.law/en/people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.person-main-block"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".name-designation-block h2")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".person-image-block a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".name-designation-block h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".name-designation-block h2")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.cssSelector(".elementor-element-c1d0b3e"));

        String practiceArea = extractor.extractLawyerText(container, new By[]{By.cssSelector(".person-related-service-block .ls-post-title")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        String email = extractor.extractLawyerText(container, new By[]{By.cssSelector(".person-phone-number a")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Spain",
                "practice_area", practiceArea,
                "email", email,
                "phone", "34915854410"
        );
    }
}

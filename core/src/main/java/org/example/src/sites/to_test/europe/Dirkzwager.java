package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Dirkzwager extends ByNewPage {

    public Dirkzwager() {
        super(
                "Dirkzwager",
                "https://www.dirkzwager.nl/en/specialists",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("c-specialist-item"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("c-specialist-item__function")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("c-specialist-item__title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("c-specialist-item__function")}, "ROLE", LawyerExceptions::roleException);
        String area = extractor.extractLawyerText(lawyer, new By[]{By.className("c-specialist-item__expertise")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("c-specialist-hero__content-container"));

        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Netherlands",
                "practice_area", area,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31263538353" : socials[1]
        );
    }
}

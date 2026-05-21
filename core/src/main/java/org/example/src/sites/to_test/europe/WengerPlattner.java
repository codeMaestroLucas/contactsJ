package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class WengerPlattner extends ByNewPage {

    public WengerPlattner() {
        super(
                "Wenger Plattner",
                "https://wenger-plattner.ch/en/team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("people-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("people-item__content")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("people-item__content")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("text--container"));

        String email = extractor.extractLawyerText(container, new By[]{By.cssSelector("p[class*='text--small-3']")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(container, new By[]{By.cssSelector("a[href^='tel:']")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Switzerland",
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "41432223800" : phone
        );
    }
}

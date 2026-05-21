package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class CarlaraAvocats extends ByNewPage {

    public CarlaraAvocats() {
        super(
                "Carlara Avocats",
                "https://www.carlara.com/avocats",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("sb-row-custom"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("element-title-inner")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("content-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("element-title-inner")}, "NAME", LawyerExceptions::nameException);
        
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("tree-container"));

        String role = extractor.extractLawyerText(container, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String rawText = container.getAttribute("innerHTML");
        String[] socials = this.getSocialsFromText(rawText);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "France",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "33153936141" : socials[1]
        );
    }
}

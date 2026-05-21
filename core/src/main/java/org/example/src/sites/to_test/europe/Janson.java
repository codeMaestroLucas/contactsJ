package org.example.src.sites.to_test.europe;

import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.exceptions.LawyerExceptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Janson extends ByNewPage {

    public Janson() {
        super(
                "Janson",
                "https://www.janson.be/team-janson",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("gallery-grid-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("gallery-caption-content")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("gallery-grid-image-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String nameFirstPage = extractor.extractLawyerText(lawyer, new By[]{By.className("gallery-caption-content")}, "NAME", LawyerExceptions::nameException).split("\n")[0].replace("*", "").trim();
        
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.id("page-section-62909832d2e32a283015e6da"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("sqsrte-large")}, "ROLE", LawyerExceptions::roleException).split("is a")[1].split("and")[0].trim();
        String[] socials = this.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", nameFirstPage,
                "role", role,
                "firm", this.name,
                "country", "Belgium",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3226753030" : socials[1]
        );
    }
}

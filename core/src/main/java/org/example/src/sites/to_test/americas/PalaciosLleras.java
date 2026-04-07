package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PalaciosLleras extends ByNewPage {

    public PalaciosLleras() {
        super(
                "Palacios Lleras",
                "https://www.palacioslleras.com/our-team/#partners",
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
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("elementor-widget-wrap"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("h3")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h2"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-icon-list-text")}, "EMAIL", LawyerExceptions::emailException);

        String link = this.openNewTab(lawyer);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Colombia",
                "practice_area", "",
                "email", email,
                "phone", "576013133600"
        );
    }
}

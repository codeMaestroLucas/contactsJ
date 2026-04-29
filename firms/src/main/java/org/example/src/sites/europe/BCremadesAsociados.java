package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BCremadesAsociados extends ByPage {

    public BCremadesAsociados() {
        super(
                "B. Cremades y Asociados",
                "https://www.cremades.com/en/lawyers/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return this.driver.findElements(By.cssSelector(".elementor-element-32e29ce"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".elementor-widget-theme-post-featured-image a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h2.elementor-heading-title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".group2singular .elementor-heading-title")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Spain",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34914237200" : socials[1]
        );
    }
}

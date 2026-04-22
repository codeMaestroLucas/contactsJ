package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class FacioYCanas extends ByPage {

    public FacioYCanas() {
        super(
                "Facio & Cañas",
                "https://fayca.com/en/partners/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("article.elementor-post"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("elementor-widget-text-editor")}, true);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h2 a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h2 a")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-widget-text-editor")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Costa Rica",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-widget-text-editor")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", "50622565555"
        );
    }
}

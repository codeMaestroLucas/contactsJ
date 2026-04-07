package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Osterling extends ByNewPage {

    public Osterling() {
        super(
                "Osterling",
                "https://osterlingfirm.com/el-equipo/socios/#secction-team",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("e-loop-item"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("elementor-widget-theme-post-title")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h4"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException);
        String practice = extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-widget-heading")}, "PRACTICE", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.id("contact_widget"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Peru",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "5116113434" : socials[1]
        );
    }
}

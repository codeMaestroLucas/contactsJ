package org.example.src.sites.to_test.africa;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Punuka extends ByNewPage {

    public Punuka() {
        super(
                "Punuka",
                "https://punuka.com/about/people/partners/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".eael-team-item"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("eael-team-member-position")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer.findElement(By.xpath("./..")), new By[]{By.className("--eael-wrapper-link-tag")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("eael-team-member-name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("eael-team-member-position")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("elementor-element-populated"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String practiceAreas = extractor.extractLawyerText(container, new By[]{By.className("elementor-widget-icon-list")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", practiceAreas,
                "email", socials[0],
                "phone", "234 1 270 4789"
        );
    }
}

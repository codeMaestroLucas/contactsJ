package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Molitor extends ByNewPage {

    public Molitor() {
        super(
                "MOLITOR",
                "https://molitorlegal.lu/our-team/#partners",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("panel-testimonial"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, false);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("panel-testimonial-person-name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("panel-testimonial-person-position")}, "ROLE", LawyerExceptions::roleException);
        String areas = extractor.extractLawyerText(lawyer, new By[]{By.className("panel-testimonial-person-function")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("container-info-person"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Luxembourg",
                "practice_area", areas,
                "email", socials[0],
                "phone", "3522972981"
        );
    }
}

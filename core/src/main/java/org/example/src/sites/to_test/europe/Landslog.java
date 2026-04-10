package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Landslog extends ByNewPage {

    public Landslog() {
        super(
                "Landslög",
                "https://en.landslog.is/personnel/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".vc_column-inner"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.tagName("p")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h5 a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h5")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.className("wpb_wrapper"));
        String[] socials = super.getSocials(container.findElements(By.tagName("p")), true);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Iceland",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath("//p[contains(strong, 'Practice areas')]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3545451600" : socials[1]
        );
    }
}

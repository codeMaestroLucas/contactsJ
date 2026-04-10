package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class FischerCia extends ByNewPage {

    public FischerCia() {
        super(
                "Fischer & Cia",
                "https://fycom.cl/en/partner/",
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
            return MyDriver.wait.findElements(By.className("premium-blog-post-outer-container"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("premium-blog-entry-title")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.className("elementor-widget-populated"));

        String role = extractor.extractLawyerText(container, new By[]{By.tagName("h6")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Chile",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("area")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "56222350000" : socials[1]
        );
    }
}

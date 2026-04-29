package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MamoTCVAdvocates extends ByNewPage {

    public MamoTCVAdvocates() {
        super(
                "Mamo TCV Advocates",
                "https://www.mamotcv.com/our-people/",
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
        return MyDriver.wait.findElements(By.cssSelector("div.vc_column_container a[href*='/our-people/']"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.id("ajax-content-wrap"));

        String role = extractor.extractLawyerText(container, new By[]{By.xpath("//div[2]/div/div/div/div/p/strong")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String name = extractor.extractLawyerText(container, new By[]{By.xpath("//div[2]/div/div/div/h2")}, "NAME", LawyerExceptions::nameException);
        String practiceArea = extractor.extractLawyerText(container, new By[]{By.cssSelector("a[href*='/practice-areas/']")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Malta",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", "35625403000"
        );
    }
}

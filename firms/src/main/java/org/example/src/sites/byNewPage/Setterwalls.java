package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Setterwalls extends ByNewPage {

    public Setterwalls() {
        super(
                "Setterwalls",
                "https://setterwalls.se/en/our-people/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElementMultipleTimes(
                By.xpath("//*[@id=\"main\"]/article/section[4]/div/div[2]/div/a"),
                10, 1
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("coworker"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("coworker__title")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("coworker__name")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("coworker__name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("coworker__title")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.className("coworker--direct-contacts"));
        String[] socials = super.getSocialsFromText(container.getText());
        String practice = extractor.extractLawyerText(driver.findElement(By.className("coworker--terms-links")), new By[]{By.xpath(".//span[contains(.,'Practice areas')]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Sweden",
                "practice_area", practice.replace("Practice areas:", "").trim(),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "46859889000" : socials[1]
        );
    }
}

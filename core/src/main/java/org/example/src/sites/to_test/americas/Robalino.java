package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Robalino extends ByNewPage {

    public Robalino() {
        super(
                "Robalino",
                "https://www.robalinolaw.com/en/profesionales",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".px-4 a"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.xpath(".//p[2]")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//p[1]")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//p[2]")}, "ROLE", LawyerExceptions::roleException).replace("Lawyer |", "").trim();

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("order-1"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String practice = extractor.extractLawyerText(container, new By[]{By.xpath(".//p[contains(@class, 'text-justify')]")}, "PRACTICE", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Ecuador",
                "practice_area", practice,
                "email", socials[0].replace("to:", ""),
                "phone", "59323810950"
        );
    }
}

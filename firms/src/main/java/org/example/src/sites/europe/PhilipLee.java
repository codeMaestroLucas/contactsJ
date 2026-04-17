package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PhilipLee extends ByNewPage {

    public PhilipLee() {
        super(
                "Philip Lee",
                "https://www.philiplee.ie/our-people/",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("kb-section-has-link"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("kb-dynamic-list-item")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("kb-section-link-overlay")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("kb-dynamic-list-item")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        String email = null;
        String phone = null;
        try {
            email = MyDriver.wait.findElement(By.xpath("//div/div/div[1]/div/div/div/div/div[3]/a")).getAttribute("href");
            phone = MyDriver.wait.findElement(By.xpath("//div/div/div[1]/div/div/div/div/div[2]/a")).getAttribute("href");
        } catch (Exception e) {}

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Ireland",
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "35312373700" : phone
        );
    }
}

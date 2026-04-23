package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BrouxelRabia extends ByNewPage {

    public BrouxelRabia() {
        super(
                "Brouxel & Rabia",
                "https://brouxelrabia.lu/our-lawyers/",
                1
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".uc_post_grid_style_one_item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".ue-item-text")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".ue-item-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".ue-item-text")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.cssSelector(".elementor-element-72ef5ea"));

        String email = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href^='mailto:']")}, "EMAIL", "href", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href^='tel:']")}, "PHONE", "href", LawyerExceptions::phoneException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "country", "Luxembourg",
                "email", email,
                "phone", phone.isEmpty() ? "+352 28 37 26 1" : phone
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
    }
}

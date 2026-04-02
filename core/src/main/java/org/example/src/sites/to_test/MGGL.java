package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MGGL extends ByNewPage {

    public MGGL() {
        super(
                "MGGL",
                "https://www.mggl.com.mx/eng/equipo/",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("raven-post-item"));
            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("elementor-widget-wrap"));
        String role = extractor.extractLawyerText(container, new By[]{By.className("raven-post-meta-terms-list")}, "ROLE", LawyerExceptions::roleException);

        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String name = extractor.extractLawyerText(container, new By[]{By.className("elementor-heading-title")}, "NAME", LawyerExceptions::nameException);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String phone = extractor.extractLawyerText(container, new By[]{By.xpath(".//span[contains(@class, 'raven-post-meta-item') and contains(text(), '+')]")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", "",
                "email", socials[0],
                "phone", phone.isEmpty() ? "525553406840" : phone
        );
    }
}

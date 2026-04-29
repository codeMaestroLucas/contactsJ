package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class PosseHerreraRuiz extends ByNewPage {

    public PosseHerreraRuiz() {
        super(
                "Posse Herrera Ruiz",
                "https://phrlegal.com/en/lawyers-phr/",
                2
        );
    }

    String currentRole = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://phrlegal.com/en/lawyers-phr/directors/";
        String url = index == 0 ? this.link : otherUrl;
        currentRole = index == 0 ? "Partner" : "Director";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("ae-post-list-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".elementor-widget-text-editor p")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("elementor-button")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.tagName("body"));

        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);
        String phone = extractor.extractLawyerAttribute(container, new By[]{By.className("ae-acf-content-wrapper")}, "PHONE", "textContent", LawyerExceptions::phoneException);
        String pa = null;
        try {
            pa = driver.findElement(By.cssSelector("a[href*='https://phrlegal.com/en/areas-abogado/l']")).getText();
        } catch (Exception e) {
            pa = "";
        }

        return Map.of(
                "link", link,
                "name", name,
                "role", currentRole,
                "firm", this.name,
                "country", "Colombia",
                "practice_area", pa,
                "email", socials[0],
                "phone", phone.isEmpty() ? "576013257300" : phone
        );
    }
}

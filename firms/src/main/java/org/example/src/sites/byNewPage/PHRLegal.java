package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PHRLegal extends ByNewPage {

    public PHRLegal() {
        super(
                "PHR Legal",
                "https://phrlegal.com/en/lawyers/?_sf_s=partner&_sft_categoria-abogado=partners",
                1
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
        try {
            return MyDriver.wait.findElements(By.cssSelector(".elementor-button-link"));
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
        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//*[@id=\"main\"]/div/section[2]/div/div[2]/div"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String phone = extractor.extractLawyerAttribute(container, new By[]{By.className("ae-acf-content-wrapper")}, "PHONE", "textContent", LawyerExceptions::phoneException);

        return Map.of(
                "link", link,
                "name", extractor.extractLawyerAttribute(container, new By[]{By.tagName("h2")}, "NAME", "textContent", LawyerExceptions::nameException),
                "role", currentRole,
                "firm", this.name,
                "country", "Colombia",
                "practice_area", "",
                "email", socials[0],
                "phone", phone.isEmpty() ? "576044488435" : phone
        );
    }
}

package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ALMTLegal extends ByPage {
    public ALMTLegal() {
        super(
                "ALMTLegal",
                "https://almtlegal.com/mumbai-partner/",
                2,
                1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://almtlegal.com/bangalore-partner/";
        String url = (index == 0) ? this.link : otherUrl;
        driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("e-con-inner"))
            );

            return driver
                    .findElement(By.xpath("//*[@id=\"content\"]/div/div/div[2]"))
                    .findElements(By.className("e-con-inner"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
            By.className("elementor-widget-container"),
            By.className("elementor-image-box-wrapper"),
            By.className("elementor-image-box-content"),
            By.className("elementor-image-box-title")
        };
        return siteUtl.iterateOverBy(byArray, lawyer).getText();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = {
            By.className("elementor-image-box-content"),
            By.className("elementor-image-box-description")
        };
        return siteUtl.iterateOverBy(byArray, lawyer).getText();
    }


    private String[] getSocials(WebElement lawyer) throws Exception {
        var outerHTML = lawyer
                .findElements(By.cssSelector("ul > li > a"));
        return super.getSocials(outerHTML);
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.link,
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "India",
                "practice_area", "",
                "email", socials[0],
                "phone", this.link.contains("mumbai") ? "+91 22 400 10000" : "+91 80 4016 0000"
        );
    }
}

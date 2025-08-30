package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ALMTLegal extends ByPage {
    public ALMTLegal() {
        super("ALMTLegal", "https://almtlegal.com/mumbai-partner/", 2);
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://almtlegal.com/bangalore-partner/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("e-con-inner")));
            return this.driver.findElement(By.xpath("//*[@id=\"content\"]/div/div/div[2]")).findElements(By.className("e-con-inner"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String getLink(WebElement lawyer) {
        return this.link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("elementor-widget-container"),
                By.className("elementor-image-box-wrapper"),
                By.className("elementor-image-box-content"),
                By.className("elementor-image-box-title")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("elementor-image-box-content"),
                By.className("elementor-image-box-description")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> outerHTML = lawyer.findElements(By.cssSelector("ul > li > a"));
            return super.getSocials(outerHTML, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        String phone = this.link.contains("mumbai") ? "+91 22 400 10000" : "+91 80 4016 0000";
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "India",
                "practice_area", "",
                "email", socials[0],
                "phone", phone
        );
    }
}
package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GillGodlontonAndGerrans extends ByNewPage {

    public GillGodlontonAndGerrans() {
        super(
                "Gill, Godlonton & Gerrans",
                "https://www.ggg.co.zw/our-people/partners/",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));

            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"post-28709\"]/div/div/div/div[2]")));
            return div.findElements(By.cssSelector("a[href*='https://www.ggg.co.zw/our-people/']"));

        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement bioColumn = driver.findElement(By.className("bio"));
        WebElement contactColumn = driver.findElement(By.className("et_pb_column_1_4"));

        String name = extractor.extractLawyerText(bioColumn, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(bioColumn, new By[]{By.tagName("h5")}, "ROLE", LawyerExceptions::roleException);

        String contactText = contactColumn.getText();
        String[] socials = super.getSocialsFromText(contactText);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Zimbabwe",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "2632427070237" : socials[1]
        );
    }
}
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

public class RCLChambersLaw extends ByNewPage {

    public RCLChambersLaw() {
        super(
                "RCL Chambers Law",
                "https://www.rclc.com.sg/our-team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[@id=\"main\"]/div/div[1]/section[2]/div/div/div/div/div/div")
            ));

            List<WebElement> lawyers = div.findElements(By.cssSelector("section.elementor-top-section"));
            lawyers.remove(lawyers.getLast());
            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.elementor-heading-title a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName1() {
        return driver.findElement(By.xpath("//*[@id=\"main\"]/div/div[1]/section/div/div[1]/div/section/div/div/div/div[1]")).getText();
    }

    private String getRole() {
        return driver.findElement(By.xpath("//*[@id=\"main\"]/div/div[1]/section/div/div[1]/div/section/div/div/div/div[2]")).getText();

    }

    private String[] getSocials() {
        try {
            WebElement div = driver.findElement(By.xpath("//*[@id=\"main\"]/div/div[1]/section/div/div[1]/div/section[1]/div/div/div/div[4]/div/ul"));
            List<WebElement> socials = div.findElements(By.tagName("li"));
            return super.getSocials(socials, true);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName1(),
                "role", this.getRole(),
                "firm", this.name,
                "country", "Singapore",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "6565399510" : socials[1]
        );
    }
}
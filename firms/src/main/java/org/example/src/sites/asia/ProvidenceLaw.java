package org.example.src.sites.asia;

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

public class ProvidenceLaw extends ByNewPage {

    public ProvidenceLaw() {
        super(
                "Providence Law",
                "https://www.providencelawasia.com/our-people/",
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
                    By.xpath("//*[@id=\"main\"]/div/div[1]/div[2]/div/div/div")
            ));
            return div.findElements(By.cssSelector("a[href*='https://www.providencelawasia.com/team/']"));

        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return link;
    }

    public String getName() {
        try {
            return driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[2]/div[1]/div/h1")).getAttribute("textContent");
        } catch (Exception e) {
            return "";
        }
    }

    private String getRole() {
        try {
            return driver.findElement(By.xpath("/html/body/div[4]/div[1]/div[2]/div[2]/div[1]/div/h2")).getAttribute("textContent");
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials() {
        try {
            WebElement lawyer = driver.findElement(By.tagName("body"));
            List<WebElement> socials = lawyer.findElements(By.tagName("a"));
            return super.getSocials(socials, false);
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
                "name", this.getName(),
                "role", this.getRole(),
                "firm", this.name,
                "country", "Singapore",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "6564381969" : socials[1]
        );
    }
}

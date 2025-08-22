package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class K1Chamber extends ByPage {
    public K1Chamber() {
        super(
            "K1 Chamber",
            "https://www.k1chamber.com/en/professionals",
            1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));

            List<WebElement> lawyers = new ArrayList<>();

            // First section
            WebElement section1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("comp-ku42xtxe")));
            lawyers.addAll(section1.findElements(By.className("T7n0L6")));

            // Second section
            WebElement section2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("comp-ku6iul7r")));
            lawyers.addAll(section2.findElements(By.className("T7n0L6")));

            // Third section
            WebElement section3 = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("comp-ku6irmoy")));
            lawyers.addAll(section3.findElements(By.className("T7n0L6")));

            if (!lawyers.isEmpty()) {
                // Invalid
                lawyers.removeFirst();
                // No email
                lawyers.remove(3);
                lawyers.remove(8);
            }

            return lawyers;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='https://www.k1chamber.com/en/'")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("Z_l5lU"),
                By.cssSelector("p")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String[] getSocials(WebElement lawyer) {
        String email = ""; String phone = "";

        WebElement div = null;
        try {
            div = lawyer.findElement(By.cssSelector("div.comp-ku4569iv1"));
            phone = div.findElement(By.cssSelector("p")).getText();
        } catch (Exception e) {}

        email = lawyer.findElement(By.cssSelector("a[href^='mailto']")).getAttribute("href");


        return new String[] { email, phone };
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
            "link", this.getLink(lawyer),
            "name", this.getName(lawyer),
            "role", "valid",
            "firm", this.name,
            "country", "Korea (South)",
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "0269568420" : socials[1]
        );
    }
}

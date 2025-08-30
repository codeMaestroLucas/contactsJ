package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
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

            WebElement section1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("comp-ku42xtxe")));
            lawyers.addAll(section1.findElements(By.className("T7n0L6")));
            WebElement section2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("comp-ku6iul7r")));
            lawyers.addAll(section2.findElements(By.className("T7n0L6")));
            WebElement section3 = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("comp-ku6irmoy")));
            lawyers.addAll(section3.findElements(By.className("T7n0L6")));

            lawyers.removeIf(lawyer -> {
                String text = lawyer.getText();
                return text.contains("K1 Chamber") || text.contains("Lee & Ko") || text.contains("Yoon & Yang");
            });

            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='https://www.k1chamber.com/en/'")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("Z_l5lU"),
                By.cssSelector("p")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            phone = lawyer.findElement(By.cssSelector("div.comp-ku4569iv1 p")).getText();
        } catch (Exception ignored) {
        }
        try {
            email = lawyer.findElement(By.cssSelector("a[href^='mailto']")).getAttribute("href");
        } catch (Exception e) {
            System.err.println("Could not extract email: " + e.getMessage());
        }
        return new String[]{email, phone};
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "Partner",
                "firm", this.name,
                "country", "Korea (South)",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "0269568420" : socials[1]
        );
    }
}
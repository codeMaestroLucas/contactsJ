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

public class SimontBraun extends ByNewPage {

    public SimontBraun() {
        super(
                "Simont Braun",
                "https://simontbraun.eu/lawyers/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.scrollToBottom(0.5);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/section[3]/div/div[2]/div/div[2]/div/div/div"))
            );

            List<WebElement> lawyers = div.findElements(By.cssSelector("a[href*='https://simontbraun.eu/lawyer/']"));

            div = driver.findElement(By.xpath("/html/body/div[1]/section[3]/div/div[2]/div/div[4]/div/div/div"));
            lawyers.addAll(div.findElements(By.cssSelector("a[href*='https://simontbraun.eu/lawyer/']")));

            div = driver.findElement(By.xpath("/html/body/div[1]/section[3]/div/div[2]/div/div[8]/div/div/div"));
            lawyers.addAll(div.findElements(By.cssSelector("a[href*='https://simontbraun.eu/lawyer/']")));

            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) {
        String firstName = lawyer.findElement(By.xpath("/html/body/div[1]/section[1]/div/div/div/div[2]/div/h1")).getAttribute("textContent");
        String lastName = lawyer.findElement(By.xpath("/html/body/div[1]/section[1]/div/div/div/div[3]/div/h1")).getAttribute("textContent");
        return (firstName + " " + lastName).trim();
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("elementor-element-6882d7e4")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.xpath("/html/body/div[1]/section[1]/div/div/div"));
        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Belgium",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty()? "3225437080" : socials[1]
        );
    }
}

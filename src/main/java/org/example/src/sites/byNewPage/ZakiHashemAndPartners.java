package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ZakiHashemAndPartners extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("position")
    };

    public ZakiHashemAndPartners() {
        super(
                "Zaki Hashem & Partners",
                "https://hashemlaw.com/our-lawyers/",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
            WebElement contentDiv = driver.findElement(By.className("all_team"));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("single_team")
                    )
            );

            js.executeScript("arguments[0].setAttribute('data-cat', '3')", contentDiv);
            lawyers.addAll(wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("single_team")
                    )
            ));

            js.executeScript("arguments[0].setAttribute('data-cat', '23')", contentDiv);
            lawyers.addAll(wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("single_team")
                    )
            ));

            js.executeScript("arguments[0].setAttribute('data-cat', '25')", contentDiv);
            lawyers.addAll(wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("single_team")
                    )
            ));

            js.executeScript("arguments[0].setAttribute('data-cat', '4')", contentDiv);
            lawyers.addAll(wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("single_team")
                    )
            ));


            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return null;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.top > h1")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.top > h5")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("content"))
                    .findElements(By.tagName("a"));
            return super.getSocials(socials, true);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.xpath("//*[@id=\"page\"]/div/div/div[1]"));
        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Egypt",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "20223999999" : socials[1]
        );
    }
}
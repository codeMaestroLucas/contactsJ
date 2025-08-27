package org.example.src.sites.byNewPage;

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

public class Madrona extends ByNewPage {
    public Madrona() {
        super(
            "Madrona",
            "https://madronaadvogados.com.br/en/our-lawyers/",
            1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.id("popup"));
    }


    protected List<WebElement> getLawyersInPage() {

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.w-full > a[href^='/en/our-lawyers/']")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("img")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("alt");
    }


    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = new By[]{
                    By.cssSelector("a[href^='/en/practice-areas/']")
            };
            WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
            return element.getText();
        } catch (Exception e) {
            return "";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.cssSelector("ul.mt-4"))
                        .findElements(By.cssSelector("li"));
            return super.getSocials(socials, true);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.xpath("//*[@id=\"__next\"]/main/div/main/div[3]/div/div[1]"));

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", "Partner",
            "firm", this.name,
            "country", "Brazil",
            "practice_area", this.getPracticeArea(div),
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}

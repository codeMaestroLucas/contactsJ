package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ShookLinAndBokP extends ByPage {

    public ShookLinAndBokP() {
        super(
                "Shook Lin & Bok",
                "https://shooklin.com.my/lawyers/senior-associates/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("ul.slb-senior-associates li")
                    )
            );
        } catch (Exception e) {
            return this.driver.findElements(By.cssSelector("li:has(.slb-partnertests-card) > a"));
        }
    }

    private String getName(WebElement lawyer) {
        return lawyer.findElement(By.tagName("h6")).getAttribute("textContent");
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
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(lawyer),
                "role", "Senior Associate",
                "firm", this.name,
                "country", "Malaysia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "60320311788" : socials[1]
        );
    }
}

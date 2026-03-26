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

public class CorneliusLaneAndMufti extends ByNewPage {

    public CorneliusLaneAndMufti() {
        super(
                "Cornelius Lane & Mufti",
                "https://clm.com.pk/CLM-Partners.html",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".services-link-widget ul li a")));
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
        return lawyer.getText().trim();
    }

    private String[] getSocials(WebElement container) {
        try {
            WebElement emailEl = container.findElement(By.cssSelector("a[href^='mailto:']"));
            return new String[]{emailEl.getText().trim(), "92518350467"};
        } catch (Exception e) {
            return new String[]{"", "92518350467"};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        this.openNewTab(lawyer);
        WebElement details = driver.findElement(By.className("details"));
        String[] socials = this.getSocials(details);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Pakistan",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
package org.example.src.sites.asia;

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
import java.util.Objects;

public class SFKSLaw extends ByPage {
    private final List<String> links = new ArrayList<>();
    private final By[] byRoleArray = {
            By.cssSelector("h1 + div")
    };


    public SFKSLaw() {
        super(
                "SFKS Law",
                "https://www.sfks.com.hk/our-people",
                50
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
            Thread.sleep(1000L);

            List<WebElement> elements = driver.findElements(By.cssSelector("a[href*='/profile/']"));
            for (WebElement element : elements) {
                String href = element.getAttribute("href");
                if (href != null && !this.links.contains(href)) {
                    this.links.add(href);
                }
            }
        }

        if (index >= this.links.size()) return;

        this.driver.get(this.links.get(index));
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.tagName("body")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{By.tagName("h1")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = new ArrayList<>();
            socials.addAll(lawyer.findElements(By.cssSelector("a[href^='mailto:']")));
            socials.addAll(lawyer.findElements(By.cssSelector("a[href^='tel:']")));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Hong Kong",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
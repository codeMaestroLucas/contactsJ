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

public class BonelliErede extends ByPage {
    public BonelliErede() {
        super(
                "BonelliErede",
                "https://www.belex.com/en/professional/",
                1
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.clickOnElement(By.className("action--bonelli-cookiebar-accept"));
    }

    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = new ArrayList<>();

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("card")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }

        String[] lawyerSections = new String[]{"partners", "local_partners", "of_counsel", "senior_counsel", "managing_associates"};

        for (String section : lawyerSections) {
            for (WebElement div : this.driver.findElements(By.className(section))) {
                lawyers.addAll(div.findElements(By.className("card")));
            }
        }
        return lawyers;
    }

    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("anag"),
                By.className("name"),
                By.cssSelector("a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("anag"),
                By.className("name"),
                By.cssSelector("a")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("addresses")).findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("anag"),
                By.className("role"),
                By.cssSelector("div > ul > li:nth-child(1)")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "-----",
                "firm", this.name,
                "country", "Italy",
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
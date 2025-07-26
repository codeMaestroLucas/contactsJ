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

public class BonelliErede extends ByPage {
    public BonelliErede() {
        super(
            "BonelliErede",
            "https://www.belex.com/en/professional/",
            1,
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "";
        String url = (index == 0) ? this.link : otherUrl;
        driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);

        siteUtl.clickOnAddBtn(By.className("action--bonelli-cookiebar-accept"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = new ArrayList<>();
        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("card"))
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }

        String[] lawyerSections = {
                "partners",
                "local_partners",
                "of_counsel",
                "senior_counsel",
                "managing_associates" // TODO: Check if this is a valid position
        };

        for (String section : lawyerSections) {
            List<WebElement> sectionDivs = driver.findElements(By.className(section));
            for (WebElement div : sectionDivs) {
                lawyers.addAll(div.findElements(By.className("card")));
            }
        }

        return lawyers;
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = {
            By.className("anag"),
            By.className("name"),
            By.cssSelector("a")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("anag"),
                By.className("name"),
                By.cssSelector("a")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("addresses"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = {
                By.className("anag"),
                By.className("role"),
                By.cssSelector("div > ul > li:nth-child(1)")
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "-----", // It's a valid one, but can't say what is his position
                "firm", this.name,
                "country", "Italy",
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}

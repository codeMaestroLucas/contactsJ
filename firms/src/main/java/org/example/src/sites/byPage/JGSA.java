package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JGSA extends ByPage {
    private final By[] byRoleArray = {
            By.className("detail-header"),
            By.cssSelector("h2")
    };


    public JGSA() {
        super(
                "JGSA",
                "https://www.jgsa.pt/en/team/Rodrigo-Jardim-Goncalves/19/#!detail",
                10
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
        } else {
            List<WebElement> aLinks = driver.findElements(By.cssSelector("ul.dropdown-menu.detail-selector-options > li > a"));
            if (index < aLinks.size()) {
                String url = aLinks.get(index).getAttribute("href");
                this.driver.get(url);
            } else {
                return;
            }
        }

        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.clickOnElement(By.cssSelector("div.cookie-bar-button > a"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement lawyerContainer = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.container > div.row.large-gutter")));
            WebElement element = siteUtl.iterateOverBy(byRoleArray, lawyerContainer);
            String role = element.getText().toLowerCase().trim();

            for (String word : role.split("\\s+")) {
                if (Arrays.asList(validRoles).contains(word)) {
                    return Collections.singletonList(lawyerContainer);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to find/validate lawyer on page: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    public String getLink(WebElement lawyer) {
        return driver.getCurrentUrl();
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("detail-header"),
                By.cssSelector("h1")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("h3-list"),
                By.cssSelector("h3")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("detail-btns"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Portugal",
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", "351213812690"
        );
    }
}
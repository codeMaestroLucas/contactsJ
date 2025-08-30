package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NPPLegal extends ByPage {
    public NPPLegal() {
        super(
                "NPP Legal",
                "https://npp.de/ceemes/en/personen/geschaeftsfuehrung.html",
                2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String url = index == 0 ? this.link : "https://npp.de/ceemes/en/personen/prokuristen.html";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        MyDriver.clickOnElement(By.cssSelector("button.cookie-btn.pull-right"));
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("employee-data")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String getLink(WebElement lawyer) {
        return driver.getCurrentUrl();
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("employee-name")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole() {
        String currentUrl = Objects.requireNonNull(driver.getCurrentUrl()).toLowerCase();
        return currentUrl.contains("prokuristen.html") ? "Director" : "Managing Director";
    }


    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = new By[]{
                    By.className("employee-company-position")
            };
            String html = extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE AREA", "outerHTML", LawyerExceptions::practiceAreaException).split("<br>")[0].trim();
            Matcher matcher = Pattern.compile("<p class=\"employee-company-position\">\\s*([^<]+)").matcher(html);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (Exception e) {
            System.err.println("Could not extract practice area: " + e.getMessage());
        }
        return "";
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            String[] outerHTMLS = lawyer
                    .findElement(By.className("employee-contact"))
                    .getAttribute("outerHTML")
                    .split("<br>");

            String email = outerHTMLS[0];
            Matcher matcher = Pattern.compile("mailto:([^\"]+)").matcher(email);
            if (matcher.find()) {
                email = matcher.group(1);
            }

            String phone = outerHTMLS[1];
            return new String[]{email, phone};
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
                "role", this.getRole(),
                "firm", this.name,
                "country", "Germany",
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
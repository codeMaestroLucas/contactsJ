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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dompatent extends ByPage {
    private final List<String> links = new ArrayList<>();

    private final By[] byRoleArray = {
            By.className("modal-header"),
            By.cssSelector("h1"),
            By.className("team-job")
    };


    public Dompatent() {
        super(
                "Dompatent",
                "https://www.dompatent.de/en/ip-experts/",
                15
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
            Thread.sleep(1000L);

            List<WebElement> lawyersLinks = driver.findElement(By.id("coach-team-widget-23"))
                    .findElement(By.className("team"))
                    .findElements(By.className("coach-team-link-expert"));
            for (WebElement lawyer : lawyersLinks) {
                links.add(lawyer.getAttribute("href"));
            }
        }

        this.driver.get(this.links.get(index));
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.id("coach-internal")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) {
        return driver.getCurrentUrl();
    }


    private String getName(WebElement lawyer) {
        try {
            By[] byArray = new By[]{
                    By.className("modal-header"),
                    By.cssSelector("h1")
            };
            String html = extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "outerHTML", LawyerExceptions::nameException);
            Pattern pattern = Pattern.compile("<h1>\\s*([^<]+?)\\s*<span");
            Matcher matcher = pattern.matcher(html);

            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (Exception e) {
            System.err.println("Could not extract name: " + e.getMessage());
        }
        return "";
    }


    private String getRole(WebElement lawyer) {
        try {
            return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
        } catch (Exception e) {
            return "Invalid role";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("contact-box"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String role = this.getRole(lawyer);

        if (role.equalsIgnoreCase("invalid role")) return "Invalid Role";

        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", role,
                "firm", this.name,
                "country", "Germany",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
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

public class Sintons extends ByPage {
    private final String[] links = {
            "https://sintons.co.uk/our-people/job-title-partner/",
            "https://sintons.co.uk/our-people/job-title-managing-partner/",
            "https://sintons.co.uk/our-people/job-title-senior-associate/"
    };

    private final By[] byRoleArray = {
            By.tagName("h3")
    };

    public Sintons() {
        super(
                "Sintons",
                "",
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(links[index]);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"content\"]/div/div[1]/div/div[2]/div/div/ul")));
            return div.findElements(By.cssSelector("li"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{By.cssSelector("a[href*='https://sintons.co.uk/our-people/']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("h1")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(String link) throws LawyerExceptions {
        if (link.contains("managing")) return "Managing Partner";
        else if (link.contains("partner")) return "Partner";
        else if (link.contains("senior-associate")) return "Senior Associate";
        return "";
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        try {
            By[] byArray = new By[]{
                    By.cssSelector("div.fusion-text p")
            };
            return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException).replace("Specialising in:", "");
        } catch (LawyerExceptions e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String role = this.getRole(Objects.requireNonNull(driver.getCurrentUrl()));

        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", role,
                "firm", this.name,
                "country", "England",
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "01912267878" : socials[1]
        );
    }
}
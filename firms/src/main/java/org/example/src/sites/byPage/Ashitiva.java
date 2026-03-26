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

public class Ashitiva extends ByPage {

    private final By[] byRoleArray = {
            By.cssSelector("div > p")
    };

    public Ashitiva() {
        super(
                "Ashitiva",
                "https://ashitivaadvocates.com/our-team/",
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
        String[] validRoles = new String[]{
                "partner", "head", "director", "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement lawyersDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"post-992165\"]/div/section/div/div/div/div/div/div/div[2]")));
            List<WebElement> lawyers = lawyersDiv.findElements(By.className("e-flex"));

            lawyersDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"post-992165\"]/div/section/div/div/div/div/div/div/div[4]")));
            lawyers.addAll(lawyersDiv.findElements(By.className("e-flex")));

            lawyersDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"post-992165\"]/div/section/div/div/div/div/div/div/div[6]")));
            lawyers.addAll(lawyersDiv.findElements(By.className("e-flex")));

            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        return "https://ashitivaadvocates.com/our-team/"; // No individual profile links
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("h3")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String text = lawyer.findElement(By.cssSelector("div > p")).getAttribute("textContent");
            String email = text.substring(text.indexOf("E:") + 3).trim();
            return new String[]{email, ""};
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Kenya",
                "practice_area", "",
                "email", socials[0],
                "phone", "2540722764732"
        );
    }
}
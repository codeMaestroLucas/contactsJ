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

public class Norens extends ByPage {

    private final By[] byRoleArray = {
            By.className("et_pb_member_position")
    };

    public Norens() {
        super(
                "Norens",
                "https://norens.se/en/about-norens/people/",
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
            WebElement lawyersDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"post-13579\"]/div/div/div/div[3]")));
            return lawyersDiv.findElements(By.className("et_pb_team_member_description"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("h4")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "tetContent", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String email = lawyer.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href");
            String phone = lawyer.findElement(By.xpath("./div/p[1]")).getText().split("\n")[0].replaceAll("[^0-9]", "");
            return new String[]{email, phone};
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.link,
                "name", this.getName(lawyer),
                "role", "", // To check manually,
                "firm", this.name,
                "country", "Sweden",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "46854587400" : socials[1]
        );
    }
}
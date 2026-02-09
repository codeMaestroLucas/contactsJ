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

public class HowseWilliams extends ByPage {
    public HowseWilliams() {
        super(
                "Howse Williams",
                "https://www.howsewilliams.com/en/our_people.php?john=1&page=1",
                6
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.howsewilliams.com/en/our_people.php?john=1&page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("staff")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("staff_more"),
                By.cssSelector("a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("staff_info"),
                By.className("staff_name"),
                By.cssSelector("h3")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("staff_info"),
                By.className("staff_desc"),
                By.cssSelector("p > span")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("staff_info"))
                    .findElement(By.className("staff_data"))
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
                "role", "Partner",
                "firm", this.name,
                "country", "Hong Kong",
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
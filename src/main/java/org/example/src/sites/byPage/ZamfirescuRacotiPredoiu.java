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

public class ZamfirescuRacotiPredoiu extends ByPage {
    private final String[] links = new String[] {
            "https://zrvp.ro/ourpeople/managing-associates/",
            "https://zrvp.ro/ourpeople/senior-associates/"
    };

    public ZamfirescuRacotiPredoiu() {
        super(
                "Zamfirescu Racoti Predoiu",
                "https://zrvp.ro/ourpeople/partners/",
                3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String url = index == 0 ? this.link : this.links[index - 1];
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        // Click on add btn
        MyDriver.clickOnElement(By.id("cookie_action_close_header"));
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("item-post-container")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("post-title"),
                By.className("entry-title"),
                By.cssSelector("a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("post-title"),
                By.className("entry-title"),
                By.cssSelector("a")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("post-position")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getEmail(WebElement lawyer) {
        // This logic is highly specific and was kept to avoid breaking the extraction.
        String element = lawyer.findElement(By.className("item-post")).getAttribute("outerHTML");
        return element
                .split("<br>")[1]
                .split("<div class=\"")[0]
                .trim();
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Romania",
                "practice_area", "",
                "email", this.getEmail(lawyer),
                "phone", "40213110517"
        );
    }
}
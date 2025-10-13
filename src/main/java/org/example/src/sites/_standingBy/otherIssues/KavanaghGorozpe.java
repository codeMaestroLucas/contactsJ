package org.example.src.sites._standingBy.otherIssues;

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

public class KavanaghGorozpe extends ByPage {
    public KavanaghGorozpe() {
        super(
                "Kavanagh Gorozpe",
                "https://www.k-g.com.mx/en/nuestro-equipo/",
                1,
                1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = new ArrayList<>();
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            //Fail to find then
            // Partners Div + Counsel
             lawyers.addAll(wait.until(
                     ExpectedConditions.presenceOfAllElementsLocatedBy(
                             By.cssSelector("div.[class='jet-engine-listing-overlay-wrap' data-url*='https://www.k-g.com.mx/en/equipo/']"))
                     )
             );

             return lawyers.subList(0, 19);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h2 > a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h2 > a")
        };
        return extractor.extractLawyerText(container, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElements(By.cssSelector("a > span > span"));
            return super.getSocials(socials, true);

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
                "role", "", // Is valid, but can't say what is his position
                "firm", this.name,
                "country", "Mexico",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
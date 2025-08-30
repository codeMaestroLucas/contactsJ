package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class BarneaAndCo extends ByNewPage {
    public BarneaAndCo() {
        super(
                "Barnea And Co",
                "https://barlaw.co.il/the-team/",
                1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = new java.util.ArrayList<>();
            lawyers.addAll(wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.leftSideblockText.partners_text.light > div.leftSideblockTextLink")
                    )
            ));

            lawyers.addAll(wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.leftSideblockText.others_text.light.other-professionals > div.leftSideblockTextLink")
                    )
            ));

            return lawyers;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.findElement(By.cssSelector("a")).getAttribute("href"));
    }

    public String getLink() {
        return driver.getCurrentUrl();
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("rightSideTitle")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole() throws LawyerExceptions {
        try {
            String roleText = driver.findElement(By.className("breadcrumb")).getText();
            return roleText.toLowerCase().contains("partner") ? "Partner" : "Counsel";
        } catch (Exception e) {
            throw LawyerExceptions.roleException("Could not find role element (breadcrumb)");
        }
    }


    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("single-attorneys-practice-areas"),
                By.className("leftSideblockTextLink")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("mo_mobile2"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement leftDiv = driver.findElement(By.className("leftSide"));
        WebElement rightDiv = driver.findElement(By.className("rightSide"));

        String[] socials = this.getSocials(leftDiv);

        return Map.of(
                "link", this.getLink(),
                "name", this.getName(rightDiv),
                "role", this.getRole(),
                "firm", this.name,
                "country", "Israel",
                "practice_area", this.getPracticeArea(leftDiv),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
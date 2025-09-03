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
import java.util.Objects;

public class OneEssexCourt extends ByNewPage {
    private final By[] byRoleArray = {
            By.className(""),
            By.cssSelector("")
    };
    private String currentRole;

    public OneEssexCourt() {
        super(
            "One Essex Court",
            "https://www.oeclaw.co.uk/mediators",
            2,
            1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.oeclaw.co.uk/arbitrators";
        String url = index == 0 ? this.link : otherUrl;
        this.currentRole = index == 0 ? "Mediator" : "Arbitrator";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[href^='https://www.oeclaw.co.uk/barristers/profile/']")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("cv-title"),
                By.className("container"),
                By.className("title"),
                By.cssSelector("h1")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "outerHTML", LawyerExceptions::nameException);
    }


    private String[] getSocials() {
        WebElement lawyer = driver.findElement(By.className("with-subnav"));
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("sub-nav"))
                    .findElement(By.className("container"))
                    .findElements(By.cssSelector("ul > li > a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("cv"));

        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.currentRole,
                "firm", this.name,
                "country", "England",
                "practice_area", this.currentRole.equals("Mediator") ? "Mediation" : "Arbitration",
                "email", socials[0],
                "phone", "4402075832000"
        );
    }
}

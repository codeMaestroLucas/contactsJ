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
import java.util.Set;

public class YoungList extends ByNewPage {
    private String currentRole;
    private final String[] roles = new String[] {
            "Mediator",
            "Arbitrator",
            "Silk"
    };
    private final String[] links = new String[] {
            "",
            "https://www.youngslist.com.au/the-list/?_counsel=27",
            "https://www.youngslist.com.au/the-list/?_counsel=28",
    };


    public YoungList() {
        super(
            "Young List",
            "https://www.youngslist.com.au/the-list/?_counsel=27",
            2,
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = this.links[index];
        this.currentRole = this.roles[index];
        String url = index == 0 ? this.link : otherUrl;
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
                            By.cssSelector("a[href^='https://www.youngslist.com.au/lawyer/']")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getThiName() throws LawyerExceptions {
        WebElement lawyer = driver.findElement(By.className("section__head"));

        By[] byArray = new By[]{
                By.cssSelector("h1")
        };

        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String[] getSocials() {
        WebElement lawyer = driver.findElement(By.className("col__contact"));
        try {
            List<WebElement> socials = lawyer
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getThiName(),
                "role", this.currentRole,
                "firm", this.name,
                "country", "Australia",
                "practice_area", this.currentRole.equals("Mediator") ? "Mediation" : "Arbitration",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "61392256777" : socials[1]
        );
    }
}

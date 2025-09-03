package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KuriBrena extends ByNewPage {
    public KuriBrena() {
        super(
                "Kuri Breña",
                "https://ksa.mx/en/team/team",
                1,
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

            List<WebElement> lawyers = new ArrayList<>();
            lawyers.addAll(wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[href^='socios/']") // partners
                    )
            ));
            lawyers.addAll(wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[href*='counsel/']") // counsels
                    )
            ));

            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }

    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h1")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText().trim();
    }

    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h3")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText().trim();
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("div.column ul:nth-of-type(3)"),
                By.cssSelector("li:nth-of-type(2)")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException).replace("-", "");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("profile-icons"))
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

        WebElement div = driver.findElement(By.className("containerd"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Mexico",
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", "525552925930"
        );
    }
}

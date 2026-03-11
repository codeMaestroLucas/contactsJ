package org.example.src.sites.byPage;

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

public class Snellman extends ByNewPage {

    private final By[] byRoleArray = {By.className("elementor-widget-text-editor")};

    public Snellman() {
        super(
                "Snellman",
                "https://snellman.com/our-people/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElementMultipleTimes(By.xpath("//*[@id=\"content\"]/div/div/div[4]/div/div/div/div[3]/a"), 5, 0.6);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate", "managing associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.elementor-element")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Error finding lawyers", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h1.presentation-head-name")}; // Re-evaluated based on standard elementor name patterns
        return extractor.extractLawyerText(div, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.presentation-head-title")};
        return extractor.extractLawyerText(div, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement div) {
        try {
            // Finding text within heading containers as seen in HTML
            String email = div.findElement(By.xpath("//h2[contains(text(), '@')]")).getText();
            String phone = div.findElement(By.xpath("//h2[contains(text(), '+')]")).getText();
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String nameFromList = lawyer.findElement(By.tagName("h2")).getText();
        openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("presentation-head-imagesection"));
        String[] socials = getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", nameFromList,
                "role", "Partner",
                "firm", this.name,
                "country", "Sweden",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "46760000000" : socials[1]
        );
    }
}

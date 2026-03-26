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

public class AzmanDavidsonAndCo extends ByNewPage {

    private final By[] byRoleArray = {By.cssSelector("h3")};

    public AzmanDavidsonAndCo() {
        super(
                "Azman Davidson & Co",
                "https://azmandavidson.com.my/our-team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("wpb_column"))
            );
            // Remove elements that DO NOT contain an <h3>
            lawyers.removeIf(element -> element.findElements(By.tagName("h3")).isEmpty());

            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyers", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("mk-image-link")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2 span")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3 span")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        try {
            email = lawyer.findElement(By.className("lawyer-email")).getText();
            String phone = lawyer.findElement(By.cssSelector("div[id^='list-20']")).getText();
            return new String[]{email, phone};
        } catch (Exception e) {
            if (email.isEmpty()) {
                email = lawyer.findElement(By.cssSelector("div[id^='list-21']")).getAttribute("textContent");
            }
            return new String[]{email, ""};
        }
    }

    private String getPractice(WebElement lawyer) {
        try {
            return lawyer.findElement(By.id("text-block-24")).getText();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String role = this.getRole(lawyer);
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("wpb_wrapper"));
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Malaysia",
                "practice_area", this.getPractice(container),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "60321640200" : socials[1]
        );
    }
}
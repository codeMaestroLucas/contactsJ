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

public class SimpsonGrierson extends ByNewPage {

    private final By[] byRoleArray = {By.className("tag-forest")};

    public SimpsonGrierson() {
        super(
                "Simpson Grierson",
                "https://www.simpsongrierson.com/people",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElementMultipleTimes(By.xpath("/html/body/div[1]/main/section[2]/div[5]/button"), 10, 0.5);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.group")));
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
        By[] byArray = {By.cssSelector("h1")};
        return extractor.extractLawyerText(div, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("button.tag")};
        return extractor.extractLawyerText(div, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement div) {
        try {
            String email = div.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href");
            String phone = div.findElement(By.cssSelector("a[href^='tel:']")).getAttribute("href");
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        openNewTab(lawyer);
        WebElement container = driver.findElement(By.cssSelector("section.bg-personal-detail"));
        String[] socials = getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", getName(container),
                "role", getRole(container),
                "firm", this.name,
                "country", "New Zealand",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "6493582222" : socials[1]
        );
    }
}

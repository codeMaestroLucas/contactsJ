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

public class Frasers extends ByNewPage {

    public Frasers() {
        super(
                "Frasers",
                "https://www.frasersvn.com/senior-legal-team",
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
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".item .team")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("content-text"));

        String role = this.getRole(container);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = super.getSocialsFromText(container.findElement(By.cssSelector(".info-content")).getText());

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(container, new By[]{By.className("title")}, "NAME", LawyerExceptions::nameException),
                "role", role,
                "firm", this.name,
                "country", "Vietnam",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "842838242733" : socials[1]
        );
    }

    private String getRole(WebElement container) throws LawyerExceptions {
        String role = extractor.extractLawyerText(container, new By[]{By.className("position")}, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }
}

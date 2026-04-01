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

public class EliasNeocleous extends ByNewPage {

    public EliasNeocleous() {
        super(
                "Elias Neocleous & Co LLC",
                "https://neo.law/people/",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("h2 > a[href*='https://neo.law/people/']")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.xpath("/html/body/div[1]/section[2]/div/div[2]/div"));

        String role = this.getRole(container);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", "",
                "role", role,
                "firm", this.name,
                "country", "Cyprus",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35725110110" : socials[1]
        );
    }

    private String getRole(WebElement container) throws LawyerExceptions {
        String role = extractor.extractLawyerText(container, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }
}

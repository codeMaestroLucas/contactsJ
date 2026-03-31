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

public class BGLegal extends ByNewPage {

    public BGLegal() {
        super(
                "BG Legal",
                "https://bg.legal/en/our-people",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("article > a.person")));
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
        WebElement container = driver.findElement(By.xpath("//*[@id=\"main\"]/section/section[1]/div/div/div[2]/div[1]/div[2]"));

        String role = extractor.extractLawyerText(driver.findElement(By.tagName("main")), new By[]{By.className("description")}, "ROLE", LawyerExceptions::roleException);

        if (!this.siteUtl.isValidPosition(role, validRoles)) {
            return "Invalid Role";
        }

        String name = extractor.extractLawyerText(container, new By[]{By.className("title")}, "NAME", LawyerExceptions::nameException);
        String[] socials = super.getSocials(container.findElements(By.className("item")), false);
        String practice = extractor.extractLawyerText(container, new By[]{By.className("expertises")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31881410800" : socials[1]
        );
    }
}

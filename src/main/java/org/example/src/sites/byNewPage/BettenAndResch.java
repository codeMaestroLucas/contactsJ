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

public class BettenAndResch extends ByNewPage {

    private final By[] byRoleArray = {
            By.className("team-title")
    };

    public BettenAndResch() {
        super(
                "Betten & Resch",
                "https://www.bettenresch.com/en-frontpage#our_team",
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
        String[] validRoles = {"partner", "counsel"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("team")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a.team-grid-image")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("hero-title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("hero-subtitle")};
        String role = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        return role.split("\n")[role.split("\n").length - 1].trim();
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = {By.xpath("//div[contains(@class, 'accordion-item-title') and contains(text(), 'Areas of Practice')]/ancestor::div[@class='accordion-item']//div[@class='acc_content']//ul")};
            return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socialElements = lawyer.findElement(By.className("address")).findElements(By.cssSelector("a, p"));
            return super.getSocials(socialElements, true);
        } catch (Exception e) {
            return new String[]{"", "49892424170"};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement content = driver.findElement(By.id("content"));

        String[] socials = this.getSocials(content);
        String currentUrl = driver.getCurrentUrl();

        return Map.of(
                "link", Objects.requireNonNull(currentUrl),
                "name", this.getName(content),
                "role", this.getRole(content),
                "firm", this.name,
                "country", "Germany",
                "practice_area", this.getPracticeArea(content),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "49892424170" : socials[1]
        );
    }
}

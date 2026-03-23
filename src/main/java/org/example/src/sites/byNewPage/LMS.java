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

public class LMS extends ByNewPage {

    public LMS() {
        super(
                "LMS",
                "https://www.lmslex.com/lawyers-archive/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.rollDown(5, 1);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("dp-dfg-item")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("entry-title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement subPage) {
        try {
            String text = subPage.findElement(By.className("professionisti-testo")).getText();
            return super.getSocialsFromText(text);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("img-lawyer-col"));

        String role = this.getRole();
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Italy",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3906696491" : socials[1]
        );
    }

    private String getRole() {
        String role = driver.findElement(By.cssSelector("nav.et-menu-nav > ul[id*='menu-lw-'] > li > a")).getAttribute("textContent");
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }
}

package org.example.src.sites.americas;

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

public class BoyneClarke extends ByNewPage {

    public BoyneClarke() {
        super(
                "Boyne Clarke",
                "https://boyneclarke.com/lawyers/",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("lawyer-profile")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions, InterruptedException {
        String link = lawyer.findElement(By.cssSelector("h3 a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[] {By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String pa = lawyer.findElement(By.cssSelector(".services-provided span")).getAttribute("textContent");

        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        String link = this.openNewTab(lawyer);

        String role = this.getRole();
        if (role.equals("Invalid Role")) return "Invalid Role";

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Canada",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "9024699500" : socials[1]
        );
    }

    private String getRole() {
        String role = driver.findElement(By.cssSelector("main p")).getAttribute("textContent");
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }
}

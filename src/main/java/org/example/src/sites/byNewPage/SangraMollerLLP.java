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

public class SangraMollerLLP extends ByNewPage {

    public SangraMollerLLP() {
        super(
                "Sangra Moller LLP",
                "https://www.sangra.com/people/",
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
        String[] validRoles = {"partner", "counsel", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("profile")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("text__title")}, true, validRoles);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("link")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("profile"));

        String name = extractor.extractLawyerText(container, new By[]{By.className("title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.className("sub-title")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerText(container, new By[]{By.cssSelector("a[href^='mailto:']")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(container, new By[]{By.cssSelector("a[href^='tel:']")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Canada",
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "6046628808" : phone
        );
    }
}
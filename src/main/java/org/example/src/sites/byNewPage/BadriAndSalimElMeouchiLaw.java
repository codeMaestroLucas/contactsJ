package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BadriAndSalimElMeouchiLaw extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("lawyer-item__title")
    };

    public BadriAndSalimElMeouchiLaw() {
        super(
                "Badri and Salim El Meouchi Law",
                "https://www.savoric.com/en/lawyers/",
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
        String[] validRoles = {"partner", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("lawyer-item")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("lawyer-item__link")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h1")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer, String name) {
        String email = "";
        try {
            email = lawyer.findElement(By.cssSelector("p.header__email > a")).getAttribute("href");
            if (email.isEmpty()) throw new LawyerExceptions("Email address is empty");
        } catch (Exception e) {
            name = TreatLawyerParams.treatNameForEmail(name);
            email = name.replace(" ", ".") + "@elmeouchi.com";
        } finally {
            return new String[]{email, ""};
        }

    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("header__intro--inner"));
        String name = this.getName(container);
        String[] generatedSocials = this.getSocials(container, name);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", this.getRole(container),
                "firm", this.name,
                "country", "Croatia",
                "practice_area", "",
                "email", generatedSocials[0],
                "phone", "38514855900"
        );
    }
}

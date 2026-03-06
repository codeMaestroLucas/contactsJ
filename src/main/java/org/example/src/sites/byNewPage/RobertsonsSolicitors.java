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

public class RobertsonsSolicitors extends ByNewPage {

    public RobertsonsSolicitors() {
        super(
                "Robertsons Solicitors",
                "https://robertsonshk.com/en/people",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    private final By[] byRoleArray = {
            By.className("people-position")
    };

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{"partner", "counsel"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("people-item")));

            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
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

    private String getName(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.className("profile-name")};
        return extractor.extractLawyerText(div, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.className("profile-position")};
        return extractor.extractLawyerText(div, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement div) {
        try {
            String email = div.findElement(By.className("email-link")).findElement(By.tagName("span")).getText();
            String phone = div.findElement(By.className("phone-link")).getText();
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("profile-area"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Hong Kong",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "85228682866" : socials[1]
        );
    }
}

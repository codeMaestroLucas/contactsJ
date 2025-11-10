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

public class DKGV extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("team-member__position")
    };

    public DKGV() {
        super(
                "DKGV",
                "",
                2
        );
    }

    private String currentRole = "";

    private String[] roles = {
        "Senior Associate",
        "Partner"
    };

    private String[] links = {
            "https://www.dgkv.com/people?first_name=&last_name=&position=3&practice_area=&industry=&language=#results",
            "https://www.dgkv.com/people?first_name=&last_name=&position=1&practice_area=&industry=&language=#results",
    };

    protected void accessPage(int index) {
        String otherUrl = this.links[index];
        this.currentRole = this.roles[index];
        this.driver.get(otherUrl);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
        return wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.className("team-list__item")
                )
        );
    }

    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        try {
            WebElement linkEl = lawyer.findElement(By.className("team-member"));
            MyDriver.openNewTab(linkEl.getAttribute("href"));
        } catch (Exception e) {
            throw LawyerExceptions.linkException("Could not find link element");
        }
    }

    private String getNameFromList(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("team-member__name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRoleFromList(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href");
            phone = lawyer.findElement(By.cssSelector("a[href^='tel:']")).getAttribute("href");
        } catch (Exception e) {
            // Socials not found
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getNameFromList(lawyer);
        String role = this.getRoleFromList(lawyer);

        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("page-header"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Bulgaria",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35929321100" : socials[1]
        );
    }
}
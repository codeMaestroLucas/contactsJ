package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WALLESS extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("team-member--title__occupation")
    };

    public WALLESS() {
        super(
                "WALLESS",
                "https://walless.com/the-team/",
                1
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.rollDown(2, 5);
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[href*='https://walless.com/the-team/']")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }

    private String getNameFromList(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("team-member--title__heading")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRoleFromList(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.xpath("//span[normalize-space()='Practice areas']/following-sibling::div/ul/li/a")};
        try {
            return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        } catch (LawyerExceptions e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        List<WebElement> liElements = driver.findElements(
                By.cssSelector("ul.section-team--contact-list > li")
        );

        List<WebElement> lastChildren = new ArrayList<>();

        for (WebElement li : liElements) {
            WebElement lastChild = li.findElement(By.xpath("./*[last()]"));
            lastChildren.add(lastChild);
        }
        return super.getSocials(lastChildren, true);
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getNameFromList(lawyer);
        String role = this.getRoleFromList(lawyer);

        this.openNewTab(lawyer);
        WebElement div = driver
                .findElement(By.id("summary"))
                .findElement(By.className("contacts-section"))
                .findElement(By.className("section-team--contact-list"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "",
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
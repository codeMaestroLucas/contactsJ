package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class PorwiszAndPartners extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector("div.team__intro > p")
    };

    public PorwiszAndPartners() {
        super(
                "Porwisz and Partners",
                "https://porwisz.pl/en/our-team/",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnAddBtn(By.cssSelector("button.cc-btn.cc-dismiss"));
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("li.card__item.mix.senior-associate, li.card__item.mix.partners")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("figure.card__image > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3.title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String role = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
        return role.split("\n")[0].trim();
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href");
            phone = lawyer.findElement(By.cssSelector("a[href^='tel:']")).getAttribute("href");
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "",
                "firm", this.name,
                "country", "Poland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "48122933600" : socials[1]
        );
    }
}
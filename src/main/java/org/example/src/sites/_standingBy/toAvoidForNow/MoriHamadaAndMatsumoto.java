package org.example.src.sites._standingBy.toAvoidForNow;

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
import java.util.stream.Collectors;

public class MoriHamadaAndMatsumoto extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector("div.field--name-field-post")
    };


    public MoriHamadaAndMatsumoto() {
        super(
                "Mori Hamada And Matsumoto",
                "https://www.morihamada.com/en/people?keywords=&sort=&jaorder=&practices=&location=&supportlang=&position=2411%2C16%2C2581%2C2626",
                1,
                15
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "of counsel",
                "senior counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("li.ctt-lawyer-other__item")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='/en/people/']")
        };
        String href = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        return "https://www.morihamada.com" + href;
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("div.field--name-field-full-name")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("div.field--name-field-lawyer-position")
        };
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return office.equalsIgnoreCase("Tokyo") ? "Japan" : office;
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            List<WebElement> areas = lawyer.findElements(By.cssSelector("div.ctt-lawyer-other__item-content__bot a"));
            return areas.stream().map(WebElement::getText).collect(Collectors.joining(", "));
        } catch (Exception e) {
            return "";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            // Extract email
            email = lawyer.findElement(By.cssSelector("div.field--name-field-lawyer-email p.field__item")).getText()
                    .replaceAll("⁠", "").trim(); // Remove invisible characters
        } catch (Exception ignored) {}
        try {
            // Extract phone
            phone = lawyer.findElement(By.cssSelector("a[href^='tel:']")).getText().trim();
        } catch (Exception ignored) {}

        return new String[]{email, phone};
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}
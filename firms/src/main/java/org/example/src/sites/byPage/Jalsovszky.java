package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Jalsovszky extends ByPage {
    private final By[] byRoleArray = {
            By.className("alcim")
    };

    public Jalsovszky() {
        super(
                "Jalsovszky",
                "https://jalsovszky.com/lawyers#partners",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "advisor", "counsel", "senior associate", "managing associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("box")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a.gomb")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.text > h2")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer, String name) {
        String email = "";
        String phone = "";
        try {
            phone = lawyer.findElement(By.cssSelector("div.contact a[href^='tel']")).getText();

            // Generate email: (firstNameLetter)(LastName)@jalsovszky.com
            // Name example: Pál Jalsovszky
            name = TreatLawyerParams.treatNameForEmail(name);

            String[] parts = name.trim().split(" ");
            if (parts.length >= 2) {
                String firstLetter = parts[0].substring(0, 1);
                String lastName = parts[parts.length - 1];
                email = firstLetter + lastName + "@jalsovszky.com";
            }
        } catch (Exception e) {
            // Ignore
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String[] socials = this.getSocials(lawyer, name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Hungary",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3618892800" : socials[1]
        );
    }
}

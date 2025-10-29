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

public class PopoviciNituStoicaAndAsociatii extends ByPage {
    public PopoviciNituStoicaAndAsociatii() {
        super(
                "Popovici Niţu StoicaAndAsociaţii",
                "",
                3
        );
    }

    private String currentRole = "";

    private String setRoleAndLink(int index) {
        String link = "";

        switch (index) {
            case 0:
                currentRole = "Partner";
                link = "https://pnsa.ro/team/partn/partners.asp";
                break;
            case 1:
                currentRole = "Managing Associate";
                link = "https://pnsa.ro/team/manag/manag.asp";
                break;
            case 2:
                currentRole = "Senior Associate";
                link = "https://pnsa.ro/team/senior/senior.asp";
                break;
        }
        return link;
    }

    protected void accessPage(int index) {
        this.driver.get(setRoleAndLink(index));
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("listing-item")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("dt.fn > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("dt.fn > a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.cssSelector("span.email > a")).getAttribute("href");
            phone = lawyer.findElement(By.cssSelector("dt.tel > span.value")).getText();
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
                "role", currentRole,
                "firm", this.name,
                "country", "Romania",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "40213177919" : socials[1]
        );
    }
}
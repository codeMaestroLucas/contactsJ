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

public class TauilAndChequer extends ByPage {
    private final By[] byRoleArray = {
            By.className("PeopleResults_card-title__wM4Hj")
    };

    public TauilAndChequer() {
        super(
                "Tauil And Chequer",
                "https://www.tauilchequer.com.br/en/people?sortCriteria=%40alphasort%20ascending&f-peopletitle=Partner&numberOfResults=48",
                2
        );
    }

    private String currentRole = "";

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.tauilchequer.com.br/en/people?sortCriteria=%40alphasort%20ascending&f-peopletitle=Counsel&numberOfResults=24";
        String url = index == 0 ? this.link : otherUrl;
        currentRole = index == 0 ? "Partner" : "Counsel";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("PeopleResults_card__Oy_u5")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("PeopleResults_card-link___bwFz")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("PeopleResults_card-name__f7wLL")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.className("PeopleResults_card-email__nu1pw")).getAttribute("href");
            phone = lawyer.findElement(By.className("PeopleResults_card-phone__J3cLX")).getAttribute("href");
        } catch (Exception e) {
            // Socials not found
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
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "556132214310" : socials[1]
        );
    }
}
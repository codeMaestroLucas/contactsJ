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

public class CoxYeats extends ByPage {

    public CoxYeats() {
        super(
                "Cox Yeats",
                "https://www.coxyeats.co.za/Partners",
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.coxyeats.co.za/Associates";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".row.item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("partnerPosition")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("partnerPosition")}, "ROLE", LawyerExceptions::roleException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);

        // Custom Logic: (firstNameLetter)(lastName)@coxyeats.co.za
        String[] parts = TreatLawyerParams.treatNameForEmail(name).split(" ");
        String email = "";
        if (parts.length >= 2) {
            String firstNameLetter = parts[0].substring(0, 1);
            String lastName = parts[parts.length - 1];
            email = firstNameLetter + lastName + "@coxyeats.co.za";
        }

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "South Africa",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.className("speciality")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", email,
                "phone", "27315368500"
        );
    }
}

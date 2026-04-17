package org.example.src.sites.europe;

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

public class WierzbowskiAndPartners extends ByNewPage {

    public WierzbowskiAndPartners() {
        super(
                "Wierzbowski & Partners",
                "https://wierzbowski.com/en/lawyers/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel",  "advisor", "managing associate", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("prawnik-box")));
            return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("pr-box")}, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Error finding lawyers", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3.h3-desc > a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("h3-desc")}, "NAME", "textContent", LawyerExceptions::nameException);

        this.openNewTab(lawyer);
        WebElement leftBox = driver.findElement(By.className("prawnik-left"));

        String email = extractor.extractLawyerAttribute(leftBox, new By[]{By.className("p-mail")}, "EMAIL", "href", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerAttribute(leftBox, new By[]{By.className("p-tel")}, "PHONE", "textContent", LawyerExceptions::phoneException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", "---",
                "firm", this.name,
                "country", "Poland",
                "practice_area", "",
                "email", email,
                "phone", phone
        );
    }
}

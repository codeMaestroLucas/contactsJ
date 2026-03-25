package org.example.src.sites.to_test;

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

public class CoEffortLaw extends ByNewPage {

    public CoEffortLaw() {
        super(
                "Co-effort Law",
                "http://www.co-effort.com/en/en_personnel/personnel_list",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("personnel_item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("info_type")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("info_list"));

        String name = extractor.extractLawyerText(container, new By[]{By.tagName("h5")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(null, new By[]{By.className("info_type")}, "ROLE", LawyerExceptions::roleException);
        String country = extractor.extractLawyerText(container, new By[]{By.xpath(".//div[contains(text(),'Office')]/following-sibling::div")}, "COUNTRY", LawyerExceptions::countryException);
        String practice = extractor.extractLawyerText(container, new By[]{By.xpath(".//div[contains(text(),'Practice Area')]/following-sibling::div")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String email = extractor.extractLawyerText(container, new By[]{By.xpath(".//span[contains(text(),'Email')]/following-sibling::span")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(container, new By[]{By.xpath(".//span[contains(text(),'Phone')]/following-sibling::span")}, "PHONE", (e) -> null);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", practice,
                "email", email,
                "phone", phone == null || phone.isEmpty() ? "862168866151" : phone
        );
    }
}

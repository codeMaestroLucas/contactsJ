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

public class ArendtMedernach extends ByNewPage {

    public ArendtMedernach() {
        super(
                "Arendt & Medernach",
                "https://www.arendt.com/about-us/our-people/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.card--people-list")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("card__subtitle")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.card__title a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement header = driver.findElement(By.className("hero--people"));

        String name = extractor.extractLawyerText(header, new By[]{By.className("hero__title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(header, new By[]{By.className("hero__subtitle")}, "ROLE", LawyerExceptions::roleException);

        WebElement contactBtn = driver.findElement(By.className("vcard-generator"));
        String email = contactBtn.getAttribute("data-email");
        String phone = contactBtn.getAttribute("data-phone");
        String country = extractor.extractLawyerText(header, new By[]{By.xpath(".//p[contains(text(), 'Office Location')]/following-sibling::ul")}, "COUNTRY", LawyerExceptions::countryException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", extractor.extractLawyerText(header, new By[]{By.className("hero__expertises")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", email,
                "phone", phone.isEmpty() ? "3524078781" : phone
        );
    }
}

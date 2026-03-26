package org.example.src.sites_to_test;

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

public class CasesLacambra extends ByNewPage {

    public CasesLacambra() {
        super(
                "Cases & Lacambra",
                "https://www.caseslacambra.com/professionals/",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("c-professionals-list__professional-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("c-professionals-list__professional-role")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileLink = lawyer.getAttribute("href");
        MyDriver.openNewTab(profileLink);
        return profileLink;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("t-professional"));

        String name = extractor.extractLawyerText(container, new By[]{By.className("c-professional-hero__name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.className("c-professional-hero__role")}, "ROLE", LawyerExceptions::roleException);

        List<WebElement> socialElements = container.findElements(By.cssSelector("a[href^='mailto:']"));
        String[] socials = super.getSocials(socialElements, false);
        String phone = extractor.extractLawyerText(container, new By[]{By.className("c-professional-contact__item-wrapper")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", driver.getCurrentUrl(),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Spain",
                "practice_area", "",
                "email", socials[0],
                "phone", phone.isEmpty() ? "376728001" : phone
        );
    }
}

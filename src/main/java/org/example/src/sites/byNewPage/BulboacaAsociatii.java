package org.example.src.sites.byNewPage;

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

public class BulboacaAsociatii extends ByNewPage {

    public BulboacaAsociatii() {
        super(
                "Bulboaca & Asociaţii",
                "https://bulboaca.com/our-team/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("li.brxe-rtpoie")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("fr-profile-card-golf__title")}, false);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("fr-profile-card-golf__button")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("fr-profile-card-golf__name")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("fr-profile-card-golf__title")}, "ROLE", "textContent", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("squad-member-about__inner"));

        String email = extractor.extractLawyerAttribute(container, new By[]{By.className("social-links-list__link-text")}, "EMAIL", "textContent", LawyerExceptions::emailException);
        String practice = extractor.extractLawyerAttribute(container, new By[]{By.className("practice-areas__services")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", Objects.requireNonNull(MyDriver.getINSTANCE().getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Romania",
                "practice_area", practice,
                "email", email,
                "phone", "40214088900"
        );
    }
}

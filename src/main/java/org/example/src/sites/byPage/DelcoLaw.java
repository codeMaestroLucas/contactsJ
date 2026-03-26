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

public class DelcoLaw extends ByPage {

    public DelcoLaw() {
        super(
                "Delco Law",
                "https://delcolaw.com/en/our-team/",
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
        By[] byRoleArray = {By.className("elementor-image-box-description")};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("grid-equipo")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        By[] byLink = {By.tagName("a")};
        By[] byName = {By.className("elementor-image-box-title")};
        By[] byRole = {By.className("elementor-image-box-description")};

        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, byLink, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerAttribute(lawyer, byName, "NAME", "textContent", LawyerExceptions::nameException),
                "role", extractor.extractLawyerAttribute(lawyer, byRole, "ROLE", "textContent", LawyerExceptions::roleException).split("\n")[0],
                "firm", this.name,
                "country", "Panama",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "5072000335" : socials[1]
        );
    }
}

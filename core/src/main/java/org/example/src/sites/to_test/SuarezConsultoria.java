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

public class SuarezConsultoria extends ByNewPage {

    private final By[] byRoleArray = {
            By.className("MMl86N")
    };

    public SuarezConsultoria() {
        super(
                "Suarez Consultoria",
                "https://www.suarezconsultoria.com/team-4",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("wixui-repeater__item")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.id("comp-ldyxcg6d"));

        String name = extractor.extractLawyerText(container, new By[]{By.className("font_0")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.className("font_8")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Colombia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "573143328252" : socials[1]
        );
    }
}

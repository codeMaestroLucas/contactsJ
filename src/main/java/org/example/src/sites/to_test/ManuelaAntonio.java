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

public class ManuelaAntonio extends ByNewPage {

    public ManuelaAntonio() {
        super(
                "Manuela António",
                "https://mantonio.net/our-team/lawyers/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("sc_team_item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("sc_team_item_subtitle")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".sc_team_item_title a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("sc_team_item_subtitle")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);

        String name = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.className("sc_layouts_title_caption")}, "NAME", LawyerExceptions::nameException);
        String email = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("//*[@id=\"ma-lawyer-profile-email-btn_sc\"]/a/span[2]/span")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Macau",
                "practice_area", "",
                "email", email,
                "phone", "85328355605"
        );
    }
}

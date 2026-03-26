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
import java.util.Objects;

public class DanielLaw extends ByNewPage {

    public DanielLaw() {
        super(
                "Daniel Law",
                "https://www.daniel-ip.com/en/team/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".et_pb_team_member")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("h4")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.clickOnElement(lawyer);
        MyDriver.waitForPageToLoad();
        return driver.getCurrentUrl();
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("et_pb_section"));

        String name = extractor.extractLawyerText(container, new By[]{By.className("et_pb_text_1_tb_body")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.className("et_pb_text_2_tb_body")}, "ROLE", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerText(container, new By[]{By.className("et_pb_toggle_0_tb_body")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String[] socials = super.getSocials(container.findElements(By.cssSelector("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "552121024212" : socials[1]
        );
    }
}

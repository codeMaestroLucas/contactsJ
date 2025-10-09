package org.example.src.sites._standingBy.toAvoidForNow;

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

public class TsarAndTsai extends ByPage {
    private final By[] byRoleArray = {
            By.className("_card-title")
    };

    public TsarAndTsai() {
        super(
                "Tsar And Tsai",
                "https://www.tsartsai.com.tw/team?lang=en",
                1,
                3
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel"};
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
        List<WebElement> lawyers = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("_people-card"))
        );
        return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        return lawyer.getAttribute("href");
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("fz-18")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String buildEmail(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        return name.toLowerCase().replaceAll("\\s+", "") + "@tsartsai.com.tw";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Taiwan",
                "practice_area", "",
                "email", this.buildEmail(name),
                "phone", "xxxxxx"
        );
    }
}
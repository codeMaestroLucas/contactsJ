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

public class BrasilSalomaoeMatthes extends ByPage {

    public BrasilSalomaoeMatthes() {
        super(
                "Brasil Salomao e Matthes",
                "https://www.brasilsalomao.com.br/en/lawyers/",
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
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("archive-lawyer__lawyer-wrapper")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("archive-lawyer__lawyer-link")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("archive-lawyer__lawyer-name")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("archive-lawyer__lawyer-partner")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = {
                    By.className("archive-lawyer__lawyer-fields-item-link")
            };
            return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        } catch (Exception e) {
            return "";
        }
    }

    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("archive-lawyer__lawyer-email")
        };
        return extractor.extractLawyerText(lawyer, byArray, "EMAIL", LawyerExceptions::emailException);
    }

    private String getPhone(WebElement lawyer) {
        try {
            By[] byArray = {
                    By.className("archive-lawyer__lawyer-telephone")
            };
            return extractor.extractLawyerText(lawyer, byArray, "PHONE", LawyerExceptions::phoneException);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String phone = this.getPhone(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Brazil",
                "practice_area", this.getPracticeArea(lawyer),
                "email", this.getEmail(lawyer),
                "phone", phone.isEmpty() ? "xxxxxx" : phone
        );
    }
}
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

public class Amorys extends ByPage {
    private final By[] byRoleArray = {
            By.className("as-job-position")
    };

    public Amorys() {
        super(
                "Amorys",
                "https://amoryssolicitors.com/meet-our-team/?gad_source=1&gclid=CjwKCAiAneK8BhAVEiwAoy2HYeFDywxj8cxZ7zyGKG2m5nFVma_hH0CZ7UvCNbnZPiHFdxtziWzn4BoCkRwQAvD_BwE",
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
        String[] validRoles = {"partner", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.et_pb_code_inner > ul > li")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("as-btn-standard-1")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.tagName("h3")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME",  "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE",  "textContent", LawyerExceptions::roleException);
    }

    private String getEmail(WebElement lawyer) {
        try {
            return lawyer.findElement(By.className("as-email")).getAttribute("textContent");
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Ireland",
                "practice_area", "",
                "email", this.getEmail(lawyer),
                "phone", "353012135940"
        );
    }
}
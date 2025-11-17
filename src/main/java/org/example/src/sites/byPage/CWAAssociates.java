package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class CWAAssociates extends ByPage {
    private final By[] byRoleArray = {
            By.className("ult-team-member-position")
    };

    public CWAAssociates() {
        super(
                "CWA Associates",
                "https://www.cwassocies.com/en/team-paris/",
                2
        );
    }

    protected void accessPage(int index) {
        String otherUrl = "https://www.cwassocies.com/en/team-lyon/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("ult-team-member-wrap")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("ult-team-member-name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getPhone(WebElement lawyer) {
        String phone = lawyer.findElement(By.xpath("//*[starts-with(@id, 'list-icon-wrap-')]/span\n")).getAttribute("textContent");
        assert phone != null;
        return phone.isEmpty() ? "330144348484" : phone;
    }

    private String generateEmail(String name) {
        name = TreatLawyerParams.treatName(name).toLowerCase().replaceAll(" ", ".");
        return name + "@cwassocies.com";
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "France",
                "practice_area", "",
                "email", this.generateEmail(this.name),
                "phone", this.getPhone(lawyer)
        );
    }

}
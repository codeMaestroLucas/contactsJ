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

public class ThomasBodstrom extends ByPage {
    private final By[] byArray = { By.className("dipl_team_member_designation") };

    public ThomasBodstrom() {

        super(
                "Thomas Bodström",
                "https://www.bodstrom.se/medarbetare/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"delägare", "grundare"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("dipl_team_content_wrapper")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.className("dipl_team_member_name") };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String role = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        return role.trim().toLowerCase().contains("grundare") ? "Founder" : "Partner";
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a[href^='mailto:']"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[] { "", "" };
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.link,
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Sweden",
                "practice_area", "",
                "email", socials[0],
                "phone", "xxxxxx"
        );
    }
}
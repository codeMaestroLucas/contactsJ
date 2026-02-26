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

public class FCLaw extends ByNewPage {

    public FCLaw() {
        super(
                "FCLaw",
                "https://fclaw.com.mo/lawyers/",
                1
        );
    }

    private final By[] byRoleArray = {By.className("lawyer-position-box")};


    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("vc_grid-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a.vc_gitem-link")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement container) throws LawyerExceptions {
        By[] byArray = {By.className("cmsms_post_title")};
        return extractor.extractLawyerText(container, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement container) throws LawyerExceptions {
        By[] byArray = {By.className("header-lawyer-position")};
        return extractor.extractLawyerText(container, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement container) {
        try {
            String email = container.findElement(By.className("header-lawyer-email")).getText().trim();
            return new String[]{email, "85328330885"};
        } catch (Exception e) {
            return new String[]{"", "85328330885"};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement header = driver.findElement(By.className("cmsms_post_header"));
        String[] socials = this.getSocials(header);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(header),
                "role", this.getRole(header),
                "firm", this.name,
                "country", "Macau",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}

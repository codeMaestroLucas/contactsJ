package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Oppenhoff extends ByNewPage {

    private final By[] byRoleArray = {By.className("h5")};

    public Oppenhoff() {
        super(
                "Oppenhoff",
                "https://www.oppenhoff.eu/en/lawyers/",
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
        String[] validRoles = {"partner", "counsel", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("list__item"))
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement container) throws LawyerExceptions {
        return extractor.extractLawyerText(container, new By[]{By.className("h3")}, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement container) throws LawyerExceptions {
        return extractor.extractLawyerText(container, new By[]{By.className("info__dot")}, "ROLE", LawyerExceptions::roleException);
    }

    private String constructEmail(String name) {
        String treated = TreatLawyerParams.treatName(name);
        String[] parts = treated.toLowerCase().split("\\s+");
        if (parts.length < 2) return parts[0] + "@oppenhoff.eu";
        return parts[0] + "." + parts[parts.length - 1] + "@oppenhoff.eu";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("card--contact"));
        String name = this.getName(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", this.getRole(container),
                "firm", this.name,
                "country", "Germany",
                "practice_area", "",
                "email", this.constructEmail(name),
                "phone", "4922120910"
        );
    }
}

package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GrimaldiAlliance extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("32", "Belgium"),
            Map.entry("44", "England"),
            Map.entry("41", "Switzerland"),
            Map.entry("33", "France")
    );

    public GrimaldiAlliance() {
        super(
                "Grimaldi Alliance",
                "https://www.grimaldialliance.com/en/our-people/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        // More than 30 clicks
        MyDriver.clickOnElementMultipleTimes(By.id("more_people"), 15, 0.5);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("people_card")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("role")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] linkBy = {By.cssSelector(".more_link a")};
        String link = extractor.extractLawyerAttribute(lawyer, linkBy, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("people_name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("role")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement contactBox = driver.findElement(By.className("people_contact"));

        String[] socials = super.getSocials(contactBox.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", this.getCountry(socials[1]),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "390230309330" : socials[1]
        );
    }

    private @Nullable String getCountry(String phone) {
        return siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, phone, "Italy");
    }
}

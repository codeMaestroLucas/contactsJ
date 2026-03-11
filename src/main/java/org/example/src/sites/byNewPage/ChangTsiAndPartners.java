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

import static java.util.Map.entry;

public class ChangTsiAndPartners extends ByNewPage {

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("taiwan", "Taiwan"),
            entry("u.s", "USA"),
            entry("korea", "Korea (South)"),
            entry("singapore", "Singapore")
    );

    public ChangTsiAndPartners() {
        super(
                "Chang Tsi & Partners",
                "https://www.changtsi.com/team?position=7",
                1
        );
    }

    private final By[] byRoleArray = {
            By.className("item-title")
    };

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("li.wow.fadeInUp")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
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

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("trc-title")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("title")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = {By.className("trc-business__list")};
            return extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE", "textContent", LawyerExceptions::practiceAreaException);
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> links = lawyer.findElements(By.tagName("a"));
            return super.getSocials(links, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("teamData-right__cont"));

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", this.getRole(container),
                "firm", this.name,
                "country", Objects.requireNonNull(this.getCountry()),
                "practice_area", this.getPracticeArea(container),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "861088369999" : socials[1]
        );
    }

    private @Nullable String getCountry() {
        String country = driver.findElement(By.xpath("/html/body/div/div/div[2]/div[2]/div[3]/div[4]")).getAttribute("textContent");
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "China");
    }
}

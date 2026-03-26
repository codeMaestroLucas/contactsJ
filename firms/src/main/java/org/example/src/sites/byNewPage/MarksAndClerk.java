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

import static java.util.Map.entry;

public class MarksAndClerk extends ByNewPage {
    private static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("aberdeen", "Scotland"),
            entry("edinburgh", "Scotland"),
            entry("glasgow", "Scotland"),
            entry("luxembourg", "Luxembourg"),
            entry("japan", "Japan"),
            entry("beijing", "China"),
            entry("hong kong", "Hong Kong"),
            entry("singapore", "Singapore"),
            entry("kuala lumpur", "Malaysia"),
            entry("korea", "South Korea"),
            entry("ottawa", "Canada"),
            entry("toronto", "Canada")
    );

    private final By[] byRoleArray = {
            By.className("title")
    };

    public MarksAndClerk() {
        super(
                "Marks & Clerk",
                "https://www.marks-clerk.com/our-people/",
                11
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) return;

        MyDriver.clickOnElement(By.className("page-next"));
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "director",
                "chair",
                "head",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("person-detail")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{By.cssSelector("h3 > a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("div.person-details > h2")
        };
        return extractor.extractLawyerText(container, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("div.person-details > h3")
        };
        return extractor.extractLawyerText(container, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("div.location-item:last-child")
        };
        String office = extractor.extractLawyerText(container, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "England");
    }

    private String getPracticeArea(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("//*[@id=\"mm-0\"]/div[1]/div/div/div/div[1]/div/div/div[2]/div[2]/ul/li[1]")
        };
        return extractor.extractLawyerText(container, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }

    private String[] getSocials(WebElement container) {
        try {
            List<WebElement> socials = container
                    .findElement(By.xpath("//*[@id=\"mm-0\"]/div[1]/div/div/div/div[1]/div/div/div[1]/div[2]/div[1]"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("person-data"));
        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
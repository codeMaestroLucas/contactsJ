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

import static java.util.Map.entry;

public class LewissSilkin extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("dublin", "Ireland"),
            entry("hong kong", "Hong Kong")
    );

    private final By[] byRoleArray = {
            By.cssSelector("span[class*='ExpertCard_card-expert__job']")
    };


    public LewissSilkin() {
        super(
                "Lewiss Silkin",
                "https://www.lewissilkin.com/experts?all_offices=Belfast%253BCardiff%253BDublin%253BGlasgow%253BHong%2520Kong%253BLeeds%253BLondon%253BManchester%253BOxford",
                1,
                3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnAddBtn(By.id("onetrust-accept-btn-handler"));

        MyDriver.clickOnElementMultipleTimes(
                By.cssSelector("button[class*='LoadMoreButton_more']"),
                10, // More than 30
                1
        );
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "director",
                "counsel",
                "senior associate",
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("ExpertCard_card-expert__KCxty")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return null;
    }

    public String getLink() {
        return driver.getCurrentUrl();
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("Heading_heading__ez5Tv")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("ProfileCardRender_profile-card__role__gUeph")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("ProfileCardRender_profile-card__location__jHo_L")};
        String country = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::roleException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "England");
    }


    private String[] getSocials() {
        String email = ""; String phone = "";

        phone = driver.findElement(By.xpath("//*[@id=\"content\"]/div/div/div/div[1]/div/div/div[3]/div/a[1]")).getAttribute("href");
        email = driver.findElement(By.xpath("//*[@id=\"content\"]/div/div/div/div[1]/div/div/div[3]/div/a[2]")).getAttribute("href");

        return new String[] { email, phone };
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.xpath("//*[@id=\"content\"]/div/div/div/div[1]/div/div"));

        String[] socials = this.getSocials();

        return Map.of(
                "link", this.getLink(),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
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

public class GuantaoLaw extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("hong kong", "Hong Kong"),
            entry("new york", "USA"),
            entry("sydney", "Australia"),
            entry("toronto", "Canada")
    );


    private final By[] byRoleArray = {
            By.className("gt_jg_hhr_itemr_ltitle")
    };


    public GuantaoLaw() {
        super(
                "Guantao Law",
                "https://www.guantao.com/en/column41?ly=0&bg=0&zw=0&py=&key=&go=goto26",
                30,
                1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.guantao.com/en/column41?ly=0&bg=0&zw=0&py=&key=&page26=" + (index + 1) + "&go=goto26";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.rollDown(1, 0.2);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("gt_jg_hhr_item")
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
        By[] byArray = {By.className("gt_team_bannercr_title")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("gt_team_bannercr_ltitle")};
        String text = extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
        return text.split("\\|")[0].trim();
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("gt_team_bannercr_ltitle")};
        String office = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "textContent", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "China");
    }


    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("gt_team_bannercr_ly")};
        String text = extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);
        return text.split("·")[0].trim();
    }


    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("gt_team_bannercr_tag"),
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "EMAIL", "textContent", LawyerExceptions::emailException).trim();
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("gt_team_bannercr"));

        return Map.of(
                "link", this.getLink(),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", this.getPracticeArea(div),
                "email", this.getEmail(div),
                "phone", ""
        );
    }
}
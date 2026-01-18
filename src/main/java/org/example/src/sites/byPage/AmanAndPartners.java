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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AmanAndPartners extends ByPage {
    private final By[] byRoleArray = {
            By.className("text-uppercase")
    };

    public AmanAndPartners() {
        super(
                "Aman And Partners",
                "https://www.aaclo.com/about/#our_people",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner",  "counsel", "principal associate", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> rows = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("row")
                    )
            );

            // Filter only rows that contain lawyer info
            List<WebElement> lawyers = new ArrayList<>();
            for (WebElement row : rows) {
                if (!row.findElements(By.className("our_people_title")).isEmpty()) {
                    lawyers.add(row);
                }
            }
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("our_people_title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String name = this.getName(lawyer);
            name = TreatLawyerParams.treatNameForEmail(name);
            String email = name.replace(" ", ".") + "@aaclo.com";
            return new String[]{email, ""};
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
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
                "country", "Ethiopia",
                "practice_area", "",
                "email", socials[0],
                "phone", "251114702868"
        );
    }
}

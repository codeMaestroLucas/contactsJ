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

public class BARDEHLEPAGENBERG extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "33", "France",
            "34", "Spain",
            "49", "Germany",
            "65", "Singapore"
    );

    public BARDEHLEPAGENBERG() {
        super(
                "BARDEHLE PAGENBERG",
                "https://www.bardehle.com/en/team",
                1,
                2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.className("ccm--save-settings"));
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            // Position 1 == Partner
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.col-xl-4[data-filter-position*='1']")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("overlay-text"),
                By.cssSelector("a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("overlay-text"),
                By.cssSelector("a > p > b")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "outerHTML", LawyerExceptions::nameException);
    }


    private String getCountry(String phone) {
        return this.siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, phone, "Not Found");
    }


    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";

        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("overlay-hover-text"))
                    .findElements(By.cssSelector("a"));

            for (WebElement social : socials) {
                String link = this.siteUtl.getContentFromTag(social);
                if (email.isEmpty() && link.contains("(at)")) email = link.replace("(at)", "@");
                else if (phone.isEmpty() && link.contains("+")) phone = link;

                if (!email.isEmpty() && !phone.isEmpty()) break;
            }
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{email, phone};
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "Partner",
                "firm", this.name,
                "country", this.getCountry(socials[1]),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
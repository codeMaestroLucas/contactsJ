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

public class AsafoAndCo extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "abidjan", "Ivory Coast",
            "casablanca", "Morocco",
            "johannesburg", "South Africa",
            "london", "England",
            "mombasa", "Kenya",
            "nairobi", "Kenya",
            "paris", "France",
            "washington dc", "USA"
    );

    private final By[] byRoleArray = {
            By.className("position")
    };


    public AsafoAndCo() {
        super(
                "Asafo And Co",
                "https://www.asafoandco.com/people/?_sft_positions=partner&sf_paged=1",
                4,
                2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.asafoandco.com/people/?_sft_positions=partner&sf_paged=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        // Click on add btn
        MyDriver.clickOnElement(By.id("cookie_action_close_header"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "managing associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("membre")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        try {
            By[] byArray = {By.cssSelector("a")};
            String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
            MyDriver.openNewTab(link);
        } catch (LawyerExceptions e) {
            System.err.println(e.getMessage());
        }
    }

    public String getLink() {
        return driver.getCurrentUrl();
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("title_container"), By.cssSelector("h1")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "outerHTML", LawyerExceptions::nameException).trim();
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("title_container"), By.className("position"), By.className("poste")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "outerHTML", LawyerExceptions::roleException).trim();
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("ville"), By.className("poste")};
        String office = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "outerHTML", LawyerExceptions::countryException).trim();
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "");
    }


    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("user_metas"), By.className("poste")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE AREA", "outerHTML", LawyerExceptions::practiceAreaException).trim();
    }


    private String[] getSocials(WebElement lawyer) {
        String email = "";

        try {
            String html = lawyer.findElement(By.className("phone")).getAttribute("outerHTML");

            String[] socials = html.split("<br>");

            for (String social : socials) {
                String[] parts = social.split("</span>");

                if (parts.length > 1) {
                    String text = parts[1]
                            .replaceFirst("\\.com.*", ".com")
                            .replace("</p>", "")
                            .trim();

                    if (text.contains("@")) {
                        email = text;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{email, ""};
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement divSocial = driver.findElement(By.className("portrait"));
        WebElement divTitle = driver.findElement(By.className("membre_content_container_wrapp"));

        String[] socials = this.getSocials(divSocial);

        return Map.of(
                "link", this.getLink(),
                "name", this.getName(divTitle),
                "role", this.getRole(divTitle),
                "firm", this.name,
                "country", this.getCountry(divSocial),
                "practice_area", this.getPracticeArea(divTitle),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
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
import java.util.List;
import java.util.Map;

public class SuarezDeVivero extends ByPage {
    private final By[] byRoleArray = {
            By.className("elementor-image-box-description")
    };

    public SuarezDeVivero() {
        super(
                "Suárez de Vivero",
                "https://suarezdevivero.com/en/professional-en/",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("elementor-image-box-wrapper")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) {
        return this.link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("elementor-image-box-title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer, String name) {
        String email = "";
        try {
            name = TreatLawyerParams.treatName(name);
            String[] nameParts = name.split(" ");
            String lastName = nameParts[nameParts.length - 1].toLowerCase().replaceAll("[^a-z]", "");
            String firstNameLetter = String.valueOf(nameParts[0].charAt(0)).toLowerCase();
            email = firstNameLetter + lastName + "@suarezdevivero.com";
        } catch (Exception e) {
            // Failed to build email
        }
        return new String[]{email, ""};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String[] socials = this.getSocials(lawyer, this.name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Spain",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34932956000" : socials[1]
        );
    }
}
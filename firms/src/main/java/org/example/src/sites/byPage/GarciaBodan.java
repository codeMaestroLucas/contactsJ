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
import java.util.Objects;

public class GarciaBodan extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "guatemala", "Guatemala",
            "costa rica", "Costa Rica",
            "honduras", "Honduras",
            "el salvador", "El Salvador"
    );

    private final By[] byRoleArray = {
            By.className("abogado-posicion")
    };


    public GarciaBodan() {
        super(
                "Garcia Bodan",
                "https://garciabodan.com/en/profesionals/",
                1,
                2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnElementMultipleTimes(
                By.id("btn-cargar-abogados"),
                4, 3
        );
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("contenedor-abogado")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("abogado-imagen")

        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("abogado-contenido"),
                By.cssSelector("h3")
        };
        return extractor.extractLawyerText(container, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement container) throws LawyerExceptions {
        return extractor.extractLawyerText(container, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("abogado-detalle"),
                By.cssSelector("p")
        };
        String country = extractor.extractLawyerAttribute(container, byArray, "COUNTRY", "outerHTML", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Nicaragua");
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("abogado-enlaces"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
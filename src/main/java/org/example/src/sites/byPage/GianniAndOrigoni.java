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

public class GianniAndOrigoni extends ByPage {
    public GianniAndOrigoni() {
        super(
                "Gianni And Origoni",
                "https://www.gop.it/people.php?lang=eng",
                1,
                3
        );

        OFFICE_TO_COUNTRY = Map.ofEntries(
                Map.entry("abu dhabi", "the UAE"),
                Map.entry("brussels", "Belgium"),
                Map.entry("london", "England"),
                Map.entry("new york", "USA"),
                Map.entry("hong kong", "Hong Kong"),
                Map.entry("shanghai", "China")
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.clickOnElement(By.className("bottone_people"));
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{
                By.className("campotab2")
        };
        String[] validRoles = new String[]{"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("tabella_risu")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("campotab6"),
                By.cssSelector("a")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("campotab1")
        };
        String nameText = extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
        String[] nameParts = nameText.split(" ");
        StringBuilder name = new StringBuilder();
        for (int i = nameParts.length - 1; i >= 0; --i) {
            name.append(nameParts[i]).append(" ");
        }
        return name.toString().trim();
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("campotab2")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("campotab5")
        };
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "Italy");
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("campotab3")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String phone = lawyer.findElement(By.className("campotab4")).getText();
            String email = lawyer.findElement(By.className("campotab7")).findElement(By.cssSelector("a")).getAttribute("href").replaceAll("\\?.*$", "");
            return new String[]{email, phone};
        } catch (Exception e) {
            System.err.println("Error getting socials for "+ e.getMessage());
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
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
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

public class McCarthyTetrault extends ByPage {
    public McCarthyTetrault() {
        super(
                "McCarthy Tetrault",
                "https://www.mccarthy.ca/en/people?sort=position",
                38
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = String.format("https://www.mccarthy.ca/en/people?page=%d&sort=position", index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(2500L);
        if (index <= 0) {
            MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));
        }
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{
                By.className("PeopleCard_body__UBZgo"),
                By.cssSelector("p")
        };
        String[] validRoles = new String[]{"partner", "counsel"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("PeopleCard_content__SGTwh")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("PeopleCard_header__gFvx1"),
                By.className("PeopleCard_link__9cYzF")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("PeopleCard_header__gFvx1"),
                By.className("PeopleCard_link__9cYzF")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("PeopleCard_body__UBZgo"),
                By.cssSelector("p")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("PeopleCard_body__UBZgo"),
                By.cssSelector("p:nth-child(2)")
        };
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        if (office.equalsIgnoreCase("new york")) {
            return "USA";
        } else if (office.toLowerCase().contains("london")) {
            return "England";
        }
        return "Canada";
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String phone = lawyer.findElement(By.className("PeopleCard_body__UBZgo")).findElement(By.cssSelector("p:nth-child(3)")).getText();
            String email = lawyer.findElement(By.className("PeopleCard_footer__q9OvK")).findElement(By.cssSelector("p")).getText();
            return new String[]{email, phone};
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
                "phone", socials[1]
        );
    }
}
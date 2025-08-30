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

import static java.util.Map.entry;

public class JonesDay extends ByPage {

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("amsterdam", "the Netherlands"),
            entry("beijing", "China"),
            entry("brisbane", "Australia"),
            entry("brussels", "Belgium"),
            entry("dubai", "the UAE"),
            entry("düsseldorf", "Germany"),
            entry("frankfurt", "Germany"),
            entry("hong kong", "Hong Kong"),
            entry("london", "England"),
            entry("madrid", "Spain"),
            entry("melbourne", "Australia"),
            entry("mexico city", "Mexico"),
            entry("milan", "Italy"),
            entry("munich", "Germany"),
            entry("paris", "France"),
            entry("perth", "Australia"),
            entry("são paulo", "Brazil"),
            entry("shanghai", "China"),
            entry("singapore", "Singapore"),
            entry("sydney", "Australia"),
            entry("taipei", "Taiwan"),
            entry("tokyo", "Japan")
           );

    private final By[] byRoleArray = {
            By.className("professional__column--right"),
            By.className("person__title")
    };

    public JonesDay() {
        super(
                "Jones Day",
                "https://www.jonesday.com/en/lawyers#sort=%40fieldz95xlevelsort%20ascending&f:@facetz95xlocation=[Amsterdam,Beijing,Brisbane,Brussels,Dubai,D%C3%BCsseldorf,Frankfurt,Hong%20Kong,London,Madrid,Milan,Munich,Paris,Perth,S%C3%A3o%20Paulo,Shanghai,Singapore,Sydney,Taipei,Tokyo]&f:@facetz95xtitle=[Partners%20%26%20Of%20Counsel]",
                17,
                3
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.jonesday.com/en/lawyers#first=" + index * 20 + "&sort=%40fieldz95xlevelsort%20ascending&f:@facetz95xlocation=[Amsterdam,Beijing,Brisbane,Brussels,Dubai,D%C3%BCsseldorf,Frankfurt,Hong%20Kong,London,Madrid,Milan,Munich,Paris,Perth,S%C3%A3o%20Paulo,Shanghai,Singapore,Sydney,Taipei,Tokyo]&f:@facetz95xtitle=[Partners%20%26%20Of%20Counsel]";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();

        new WebDriverWait(this.driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("professional__container")));

        if (index == 0) {
            MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));
        } else {
            Thread.sleep(3500L);
        }
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.className("professional__container")
            ));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String getLink(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) {
        try {
            By[] byArray = {
                    By.className("professional__column--right"),
                    By.className("person__name")
            };
            return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
        } catch (Exception e) {
            return "";
        }
    }

    private String getRole(WebElement lawyer) {
        try {
            return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
        } catch (Exception e) {
            return "";
        }
    }

    private String getCountry(WebElement lawyer) {
        try {
            By[] byArray = {
                    By.className("professional__column--right"),
                    By.cssSelector("div.person__row:nth-child(2)"),
                    By.className("person__meta")
            };
            String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
            return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "USA");
        } catch (Exception e) {
            return "";
        }
    }


    private String getPracticeArea(WebElement lawyer) {
        try {
            WebElement element = lawyer.findElement(By.xpath("//span[contains(text(), 'Practice:')]"));
            return element.getText().replace("Practice:", "").trim().split(";")[0];
        } catch (Exception e) {
            return "";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.xpath(".//span[contains(text(), '@')]")).getText();
            phone = lawyer.findElement(By.className("person__phone-listing")).getText();
        } catch (Exception ignored) {
        }
        return new String[]{email, phone};
    }


    public Object getLawyer(WebElement lawyer) throws LawyerExceptions {
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
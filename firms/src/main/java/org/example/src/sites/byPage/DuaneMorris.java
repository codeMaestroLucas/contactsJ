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

public class DuaneMorris extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("hanoi", "Vietnam"),             //  opt 8
            Map.entry("ho chi minh city", "Vietnam"),  //  opt 10
            Map.entry("london", "England"),            //  opt 13
            Map.entry("myanmar", "Myanmar"),           //  opt 16
            Map.entry("shanghai", "China"),            //  opt 23
            Map.entry("singapore", "Singapore"),       //  opt 25
            Map.entry("sydney", "Australia")           //  opt 27
    );


    private final By[] byRoleArray = {
            By.tagName("em")
    };

    public DuaneMorris() {
        super(
                "Duane Morris",
                "https://www.duanemorris.com/site/listings/peoplesearch.html",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

//        Select officesFilter = new Select(driver.findElement(By.id("office")));
//        officesFilter.selectByVisibleText("London");

        // Office opt
        MyDriver.clickOnElement(By.xpath("//*[@id=\"office\"]/option[14]"));

        // Btn submit
        MyDriver.clickOnElement(By.xpath("//*[@id=\"searchdataform-a\"]/div[3]/div/button[1]"));

    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"chairman", "partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("rosterline")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.col-md-4 > a > span")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
//        By[] byArray = {By.cssSelector("div.col-md-6")};
//        String country = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
//        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
        return "England";
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.cssSelector("button.btn_img_only")).getAttribute("textContent");
            phone = lawyer.findElement(By.cssSelector("a[href^='tel:']")).getAttribute("textContent");
        } catch (Exception e) {}
        return new String[]{email, phone};
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
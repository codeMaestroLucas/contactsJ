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

public class HillDickinson extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("852", "Hong Kong"),
            entry("357", "Cyprus"),
            entry("39", "Italy"),
            entry("30", "Greece"),
            entry("65", "Singapore")
    );

    private final By[] byRoleArray = {
            By.cssSelector("p:nth-of-type(2)"),
    };


    public HillDickinson() {
        super(
                "Hill Dickinson",
                "https://www.hilldickinson.com/people?title=&position%5B%5D=31&position%5B%5D=95&position%5B%5D=6492&position%5B%5D=118&position%5B%5D=6364",
                1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.hilldickinson.com/people?title=&position%5B31%5D=31&position%5B95%5D=95&position%5B6492%5D=6492&position%5B118%5D=118&position%5B6364%5D=6364&page=" + index;
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnElement(By.id("ccc-recommended-settings"));
        MyDriver.clickOnElementMultipleTimes(
                By.xpath("/html/body/main/astro-island[2]/div[1]/div/div/div/div/div/div[3]/button"),
                35, 0.4);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "chair", "head", "director", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[class*='card-person']")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        return lawyer.getAttribute("href");
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("p")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    private String getCountry(String phone) {
        return siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, phone, "England");
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(socials[1]),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]);
    }
}
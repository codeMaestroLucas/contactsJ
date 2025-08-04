package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Map.entry;

public class PaulHastings extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector("div.flex.flex-col"),
            By.cssSelector("p"),
    };


    public PaulHastings() {
        super(
            "Paul Hastings",
            "https://www.paulhastings.com/professionals?refinementList%5Boffices.title%5D%5B0%5D=Beijing&refinementList%5Boffices.title%5D%5B1%5D=Brussels&refinementList%5Boffices.title%5D%5B2%5D=Frankfurt&refinementList%5Boffices.title%5D%5B3%5D=Hong%20Kong&refinementList%5Boffices.title%5D%5B4%5D=London&refinementList%5Boffices.title%5D%5B5%5D=Paris&refinementList%5Boffices.title%5D%5B6%5D=Seoul&refinementList%5Boffices.title%5D%5B7%5D=Shanghai&refinementList%5Boffices.title%5D%5B8%5D=Tokyo&refinementList%5Boffices.title%5D%5B9%5D=S%C3%A3o%20Paulo&refinementList%5Boffices.title%5D%5B10%5D=Abu%20Dhabi",
            12,
            1000
        );

        OFFICE_TO_COUNTRY = Map.ofEntries(
                entry("abu dhabi", "the UAE"),
                entry("beijing", "China"),
                entry("brussels", "Belgium"),
                entry("frankfurt", "Germany"),
                entry("hong kong", "Hong Kong"),
                entry("london", "England"),
                entry("paris", "France"),
                entry("são paulo", "Brazil"),
                entry("seoul", "Korea (South)"),
                entry("shanghai", "China"),
                entry("tokyo", "Japan")
        );

    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.paulhastings.com/professionals?page=" + (index + 1) + "&refinementList%5Boffices.title%5D%5B0%5D=Beijing&refinementList%5Boffices.title%5D%5B1%5D=Brussels&refinementList%5Boffices.title%5D%5B2%5D=Frankfurt&refinementList%5Boffices.title%5D%5B3%5D=Hong%20Kong&refinementList%5Boffices.title%5D%5B4%5D=London&refinementList%5Boffices.title%5D%5B5%5D=Paris&refinementList%5Boffices.title%5D%5B6%5D=Seoul&refinementList%5Boffices.title%5D%5B7%5D=Shanghai&refinementList%5Boffices.title%5D%5B8%5D=Tokyo&refinementList%5Boffices.title%5D%5B9%5D=S%C3%A3o%20Paulo&refinementList%5Boffices.title%5D%5B10%5D=Abu%20Dhabi";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        Thread.sleep(3000L); // Necessary sleep

        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        this.siteUtl.clickOnAddBtn(By.cssSelector("button.dnqsUT"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> unTreatedLawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("ul.w-full > li")
                    )
            );
            List<WebElement> lawyers = IntStream.range(0, unTreatedLawyers.size())
                    .filter(i -> i % 2 == 0) // keep even indexes
                    .mapToObj(unTreatedLawyers::get)
                    .collect(Collectors.toList());

            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(String name) {
        name = name.toLowerCase();
        String[] names = name.split(" ");
        // First + Last names
        return "https://www.paulhastings.com/professionals/" + names[0] + names[names.length - 1];
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("div[contenttype='professional'] > h2")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='/offices/']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, element.getText());
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        String lawyerName = this.getName(lawyer);
        return Map.of(
            "link", this.getLink(lawyerName),
            "name", lawyerName,
            "role", this.getRole(lawyer),
            "firm", this.name,
            "country", this.getCountry(lawyer),
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1]);
    }
}

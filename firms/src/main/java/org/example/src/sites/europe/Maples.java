package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

public class Maples extends ByNewPage {

    public Maples() {
        super(
                "Maples",
                "https://maples.com/people",
                1
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("cayman islands", "the Cayman Islands"),
            entry("dublin", "Ireland"),
            entry("hong kong", "China"),
            entry("montreal", "Canada"),
            entry("luxembourg", "Luxembourg"),
            entry("london", "England"),
            entry("singapore", "Singapore"),
            entry("dubai", "the UAE"),
            entry("jersey", "Jersey"),
            entry("delaware", "USA"),
            entry("netherlands", "the Netherlands"),
            entry("british virgin islands", "the BVI"),
            entry("bermuda", "Bermuda"),
            entry("boston", "USA"),
            entry("abu dhabi", "the UAE")
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

        // More than 80 rolls
        MyDriver.rollDownToBottom(0.4);
        MyDriver.clickOnElementMultipleTimes(By.xpath("/html/body/div[4]/div[2]/div/div[1]/div[2]/div/div/button"), 10, 0.2);
        MyDriver.rollDownToBottom(0.4);

    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = driver.findElements(By.cssSelector("a[href*='https://maples.com/people/']"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("div.elementor-heading-title")}, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.elementor-heading-title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".elementor-element-63a184c .elementor-heading-title")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("li span.elementor-icon-list-text")};
        String country = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Ireland");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("a"));
            String[] socials1 = super.getSocials(socials, false);

            socials1[0] = socials1[0].isEmpty() ? "searchEmail" : socials1[0];

            return socials1;
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String role = this.getRole(lawyer);
        String country = this.getCountry(lawyer);
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.xpath("/html/body/article/div[2]/div/div[1]/div[2]/div"));
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0].split("\\?")[0],
                "phone", socials[1].isEmpty() ? "35316192104" : socials[1]
        );
    }
}
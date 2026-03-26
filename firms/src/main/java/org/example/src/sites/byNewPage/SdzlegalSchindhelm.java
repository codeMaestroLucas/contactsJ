package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

public class SdzlegalSchindhelm extends ByNewPage {

    public SdzlegalSchindhelm() {
        super(
                "SDZLEGAL Schindhelm",
                "",
                13,
                3
        );
    }

    private String currentCountry = "";

    private final String[] validRoles = {"partner", "counsel", "principal", "director", "head", "senior associate"};


    private static final Map<String, String> COUNTRY_LINKS = Map.ofEntries(
            Map.entry("Poland", "https://pl.schindhelm.com/en/team"),
            Map.entry("Austria", "https://at.schindhelm.com/en/team"),
            Map.entry("Belgium", "https://be.schindhelm.com/en/team"),
            Map.entry("Bulgaria", "https://bg.schindhelm.com/en/team"),
            Map.entry("China", "https://cn.schindhelm.com/en/team"),
            Map.entry("the Czech Republic", "https://cz.schindhelm.com/en/team"),
            Map.entry("France", "https://fr.schindhelm.com/en/team"),
            Map.entry("Germany", "https://de.schindhelm.com/en/team"),
            Map.entry("Hungary", "https://hu.schindhelm.com/en/team"),
            Map.entry("Italy", "https://it.schindhelm.com/en/team"),
            Map.entry("Romania", "https://ro.schindhelm.com/en/team"),
            Map.entry("Slovakia", "https://sk.schindhelm.com/en/team"),
            Map.entry("Turkey", "https://tr.schindhelm.com/en/team")
    );

    private static final List<String> COUNTRY_NAMES = new ArrayList<>(COUNTRY_LINKS.keySet());

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String country = COUNTRY_NAMES.get(index);
        String url = COUNTRY_LINKS.get(country);

        this.currentCountry = country;

        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("employeediv")
                    )
            );

            return lawyers.stream()
                    .filter(lawyer -> !lawyer.findElements(
                            By.cssSelector("a[href*='en/team']")
                    ).isEmpty())
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("div.employeedetaildiv > a.employeebutton.w-button[href*='en/team']")).getAttribute("href");
        MyDriver.openNewTab(link);
        return null;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("employeetext")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("a"));
            return super.getSocials(socials, true);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        this.openNewTab(lawyer);

        WebElement detailBox = driver.findElement(By.className("teamdetail"));

        String role = this.getRole(detailBox);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(detailBox);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", currentCountry,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }

    private String getRole(WebElement detailBox) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("p.teamdetail-shortinfo")};
        String role = extractor.extractLawyerAttribute(detailBox, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }
}

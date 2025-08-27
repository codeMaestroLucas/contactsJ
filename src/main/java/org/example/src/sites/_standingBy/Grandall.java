package org.example.src.sites._standingBy;

import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

public class Grandall extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("cambodia", "Cambodia"),
            entry("hong kong", "Hong Kong"),
            entry("madrid", "Spain"),
            entry("malaysia", "Malaysia"),
            entry("new york", "USA"),
            entry("paris", "France"),
            entry("stockholm", "Sweden"),
            entry("sydney", "Australia"),
            entry("toronto", "Canada"),
            entry("uzbekistan", "Uzbekistan")
    );


    String[] validRoles = new String[]{
            "partner",
            "counsel"
    };


    private final By[] byRoleArray = {
            By.className("zw"),
            By.cssSelector("span")
    };


    public Grandall() {
        super(
            "Grandall",
            "http://www.grandall.com.cn/en/lsss/list.aspx?Key=&Position=",
            44,
            1000
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) return;

        WebElement nextBtn = driver.findElement(By.cssSelector(".p_page .a_next"));
        //TODO: Failing to click on next and load new lawyers

        MyDriver.clickOnElement(nextBtn);
        Thread.sleep(3000L);
        MyDriver.waitForPageToLoad();
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.team > ul.ul > li.clearfix > a")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h3")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h3 > em")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String role = element.getText();
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }


    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = new By[]{
                    By.className("zyly"),
                    By.cssSelector("span")
            };
            WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
            return element.getText();
        } catch (Exception e) {
            return "";
        }
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("add")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return OFFICE_TO_COUNTRY.getOrDefault(element.getText().trim().toLowerCase(), "China");
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElements(By.cssSelector("span"));
            return super.getSocials(socials, true);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("info"));

        String role = this.getRole(div);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", role,
            "firm", this.name,
            "country", this.getCountry(div),
            "practice_area", this.getPracticeArea(div),
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }

    public static void main(String[] args) {
        Grandall x = new Grandall();
        x.searchForLawyers();
    }
}

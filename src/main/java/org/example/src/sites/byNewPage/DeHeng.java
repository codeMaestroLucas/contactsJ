package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
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

public class DeHeng extends ByNewPage {

    public DeHeng() {
        super(
                "DeHeng",
                "https://www.dehenglaw.com/EN/Team/0005.aspx",
                31
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("the hague", "the Netherlands"),
            entry("new york", "USA"),
            entry("paris", "France"),
            entry("brussels", "Belgium"),
            entry("dubai", "the UAE"),
            entry("silicon valley", "USA"),
            entry("almaty", "Kazakhstan"),
            entry("vientiane", "Laos"),
            entry("seoul", "Korea (South)"),
            entry("tokyo", "Japan"),
            entry("singapore", "Singapore"),
            entry("riyadh", "Saudi Arabia"),
            entry("jakarta", "Indonesia"),
            entry("kuala lumpur", "Malaysia"),
            entry("hanoi", "Vietnam")
    );

    private final By[] byRoleArray = {
            By.tagName("span")
    };


    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        } else {
            MyDriver.clickOnElement(By.xpath("//*[@id=\"aspnetForm\"]/div[4]/div/div[3]/div/ul/li[6]/a"));
            MyDriver.waitForPageToLoad();
            Thread.sleep(2000);
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "head", "founder", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.lawyer_box ul li")));

            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("lawyer_name")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("lawyer_site")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.xpath("//div[contains(text(), 'Locations')]//span")};
        String country = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "textContent", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "China");
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.xpath("//div[contains(@class, 'lawyer_detail_line') and contains(text(), 'Practice area')]")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String email = lawyer.findElement(By.xpath("//div[contains(text(), 'Email')]//span")).getText();
            String phone = lawyer.findElement(By.xpath("//div[contains(text(), 'Tel')]//span")).getText();
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.cssSelector("div.simple_box > div.width8 div.padding15"));

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", this.getRole(container),
                "firm", this.name,
                "country", this.getCountry(container),
                "practice_area", this.getPracticeArea(container),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}

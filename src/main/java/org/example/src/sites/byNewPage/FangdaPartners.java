package org.example.src.sites.byNewPage;

import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FangdaPartners extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("title_location"),
            By.cssSelector("span")
    };


    public FangdaPartners() {
        super(
            "Fangda Partners",
            "https://www.fangdalaw.com/team/",
            1,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);


        // Position opt
        WebElement dropdown = driver.findElement(By.xpath("//*[@id='category_list']/div[2]/select"));
        Select select = new Select(dropdown);
        select.selectByVisibleText("All");


        MyDriver.clickOnElementMultipleTimes(
                driver.findElement(By.className("search_see_more")),
                4, 1
        ); // Actually it has 32 iterations
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",

        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("parthner_name")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        MyDriver.openNewTab(element.getAttribute("href"));
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
                By.className("single-people-dl"),
                By.cssSelector("div")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("single-people-dl"),
                By.className("location")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String country = element.getText();
        return country.toLowerCase().contains("hong kong") ? "Hong Kong" : "China";
    }


    private String[] getSocials(WebElement lawyer) {
        String email = ""; String phone = "";

        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("div"));
            for (WebElement social : socials) {
                String content = social.getText();
                if (content.contains("@")) email = content;
                else if (content.contains("+")) phone = content.split("/")[0];
            }

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }

        return new String[]{ email, phone };
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("profile_header_wrapper"));

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", this.getRole(div),
            "firm", this.name,
            "country", this.getCountry(div),
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}

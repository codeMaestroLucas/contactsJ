package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Yulchon extends ByPage {

    private final By[] byRoleArray = {
            By.cssSelector(".mb_name p span")
    };

    public Yulchon() {
        super(
                "Yulchon",
                "https://www.yulchon.com/en/professionals/professionals-search.do",
                5
        );
    }

    private String[] vowels = {"a", "e", "i", "o", "u"};

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
            Thread.sleep(1000L);
        }

        WebElement input = driver.findElement(By.id("searching"));
        input.clear();
        input.sendKeys(this.vowels[index]);
        input.sendKeys(Keys.ENTER);
        Thread.sleep(3000L);
        MyDriver.clickOnElementMultipleTimes(By.className("btn_list_more"), 15, 0.4);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "chairman", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("mb_info_box"))
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='fnDetailView']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".mb_name p:first-child")};
        String name1 = extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
        String[] parts = name1.split(", ");
        return parts[parts.length - 1] + " " + parts[0];
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("mb_email")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "EMAIL", "href", LawyerExceptions::emailException).replace("mailto:", "");
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Korea (South)",
                "practice_area", "",
                "email", this.getEmail(lawyer),
                "phone", "8225285200"
        );
    }
}

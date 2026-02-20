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

public class YoonAndYang extends ByPage {

    private final By[] byRoleArray = {
            By.className("grade")
    };

    public YoonAndYang() {
        super(
                "YoonAndYang",
                "https://yoonyang.co.kr/eng/professionals/members?lang=en",
                5
        );
    }

    private final String[] letters = {"a", "e", "i", "o", "u"};

    @Override
    protected void accessPage(int index) throws InterruptedException {
        WebElement input = null;
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        }
        input = driver.findElement(By.id("word"));
        assert input != null;
        input.clear();
        input.sendKeys(letters[index]);
        input.sendKeys(Keys.ENTER);
        Thread.sleep(2000);

        // More than 50
        MyDriver.clickOnElementMultipleTimes(By.cssSelector("div#moreButton > button"), 10, 0.8);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "adviser", "advisor", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("box-info-lawyer")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector(".contact span"));
            return super.getSocials(socials, true);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Korea (South)",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "260037000" : socials[1]
        );
    }
}

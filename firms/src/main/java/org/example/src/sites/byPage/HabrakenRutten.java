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

public class HabrakenRutten extends ByPage {

    private final By[] byRoleArray = {
            By.xpath("//div[contains(@id, 'comp-')]/p[1]\n")
    };

    public HabrakenRutten() {
        super(
                "Habraken Rutten",
                "https://www.habrakenrutten.com/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner", "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement lawyersDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"comp-makshtz1\"]/div")));
            List<WebElement> lawyers = lawyersDiv.findElements(By.cssSelector("div.wixui-box"));
            List<WebElement> validLawyers = this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
            // remove all odd-indexed lawyers
            for (int i = validLawyers.size() - 1; i >= 0; i--) {
                if (i % 2 == 1) { // odd index
                    validLawyers.remove(i);
                }
            }

            return validLawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{By.cssSelector("a[href*='https://www.habrakenrutten.com/team/']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("h2"),
                By.tagName("a")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> div = lawyer.findElements(By.tagName("p"));
            return super.getSocials(div, true);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
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
                "country", "the Netherlands",
                "practice_area", "",
                "email", socials[0].replace("e: ", ""),
                "phone", socials[1].isEmpty() ? "310883744900" : socials[1]
        );
    }
}
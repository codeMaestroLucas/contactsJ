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

public class PricaAndPartners extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector(".et_pb_text_4.position .et_pb_text_inner")
    };

    public PricaAndPartners() {
        super(
                "PricaAndPartners",
                "https://pricapartners.com/people/",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

        MyDriver.clickOnElement(By.xpath("//*[@id=\"et-boc\"]/div/div/div[4]/div[2]/div/div"));
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("people-search-row")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("read-more-button")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".et_pb_text_3.name .et_pb_text_inner")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";

        try {
            List<WebElement> socials = lawyer.findElements(By.className("et_pb_blurb_description"));
            return super.getSocials(socials, true);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Serbia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "381112621741" : socials[1]
        );
    }
}
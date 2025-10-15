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

public class AlukoAndOyebode extends ByPage {
    private final By[] byRoleArray = {
            By.className("part-name")
    };

    private final String[] xpaths = {
           "/html/body/div[2]/main/section[3]/div/div/div/div[2]/ul/li[1]/a",    // a
           "/html/body/div[2]/main/section[3]/div/div/div/div[2]/ul/li[5]/a",    // e
           "/html/body/div[2]/main/section[3]/div/div/div/div[2]/ul/li[9]/a",    // i
           "/html/body/div[2]/main/section[3]/div/div/div/div[2]/ul/li[15]/a",   // o
           "/html/body/div[2]/main/section[3]/div/div/div/div[2]/ul/li[21]/a",   // u
    };

    public AlukoAndOyebode() {
        super(
                "Aluko & Oyebode",
                "https://www.aluko-oyebode.com/attorney/",
                5
        );
    }

    protected void accessPage(int index) {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();

            MyDriver.clickOnAddBtn(By.className("cc-allow"));
        }

        MyDriver.clickOnElement(By.xpath(xpaths[index]));
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "senior associate", "managing associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("related-p")
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
        By[] byArray = {By.tagName("h2")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        try {
            String fullName = this.getName(lawyer);
            String[] nameParts = fullName.split(", ");
            if (nameParts.length > 1) {
                String lastName = nameParts[0].toLowerCase().trim();
                String firstName = nameParts[1].toLowerCase().trim();
                email = firstName + "." + lastName + "@aluko-oyebode.com";
            }
        } catch (Exception e) {
            // Social not found, ignore
        }
        return new String[]{email, ""};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}
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

public class DGKV extends ByNewPage {
    String[] validRoles = new String[]{
            "partner",
            "counsel",
            "senior associate"
    };

    String[] links = {
            "", // Start from the counsels, to register then at once and then remove this link
            "https://dgkv.com/lawyers?first_name=&last_name=&position=1&practice_area=&industry=&language=#results",
            "https://dgkv.com/lawyers?first_name=&last_name=&position=3&practice_area=&industry=&language=#results"
    };


    public DGKV() {
        super(
                "DGKV",
                "https://dgkv.com/lawyers?first_name=&last_name=&position=2&practice_area=&industry=&language=#results",
                3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String url = (index == 0) ? this.link : this.links[index];
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        MyDriver.clickOnAddBtn(By.id("c-p-bn"));
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("team-member--alt")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }

    public String getLink() {
        return driver.getCurrentUrl();
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("page-header__title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("page-header__subtitle")};
        String role = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("contact-info"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("page-header"));

        String role = this.getRole(div);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", this.getLink(),
                "name", this.getName(div),
                "role", role,
                "firm", this.name,
                "country", "Bulgaria",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3592932161" : socials[1]
        );
    }
}
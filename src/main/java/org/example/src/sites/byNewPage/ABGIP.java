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

public class ABGIP extends ByNewPage {
    private final String[] validRoles = new String[]{
            "partner"
    };

    // TO many errors in the collect of lawyers, but is working
    public ABGIP() {
        super(
            "ABG IP",
            "https://abg-ip.com/tomas-llamas/",
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnAddBtn(By.id("cookie_action_close_header"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> until = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[href^='https://abg-ip.com/']")
                    )
            );
            return until.subList(14, until.size() - 1);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("awsm-personal-info"),
                By.cssSelector("h3")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("awsm-personal-info"),
                By.cssSelector("span")
        };
        String role = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.cssSelector("div.awsm-grid.list-style.style-1.grid-2-col"));
        String role = this.getRole(div);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", role,
                "firm", this.name,
                "country", "Spain",
                "practice_area", "",
                "email", socials[0],
                "phone", "34914171300"
        );
    }
}

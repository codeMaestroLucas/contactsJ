package org.example.src.sites.byNewPage;

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

public class EBN extends ByNewPage {
    String[] validRoles = new String[]{
            "partner",
            "counsel",
            "director",
            "senior associate"
    };


    public EBN() {
        super(
            "EBN",
            "https://www.ebnlaw.co.il/team-members/",
            1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.member > a")));

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("team-member-masthead__info"),
                By.className("team-member-masthead__name")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("team-member-masthead__info"),
                By.className("team-member-masthead__position")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String role = element.getText();
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }


    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = new By[]{
                    By.className("hero__practice-areas"),
                    By.cssSelector("a[href^='https://www.ebnlaw.co.il/practice-areas/']")
            };
            WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
            return element.getText();
        } catch (Exception e) {
            return "";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("team-member-masthead__contact"))
                        .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("team-member-masthead__container"));

        String role = this.getRole(div);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", role,
            "firm", this.name,
            "country", "Israel",
            "practice_area", this.getPracticeArea(div),
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "97237770111" : socials[1]
        );
    }
}

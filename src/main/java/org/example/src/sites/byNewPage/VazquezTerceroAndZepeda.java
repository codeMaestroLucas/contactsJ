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

public class VazquezTerceroAndZepeda extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("elementor-icon-box-description")
    };

    public VazquezTerceroAndZepeda() {
        super(
                "Vazquez Tercero And Zepeda",
                "https://vtz.mx/team/",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }
    private final String[] validRoles = {"partner", "counsel", "advisory"};

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector(".plus-slide-content .elementor-section-boxed")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("button-link-wrap")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h1.elementor-icon-box-title > a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("elementor-icon-box-description")};
        String role = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = {By.cssSelector("h2.elementor-heading-title")};
            String text = extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
            return text.split(":")[1].trim();
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            List<WebElement> items = lawyer.findElements(By.cssSelector("ul.elementor-icon-list-items li"));
            for (WebElement item : items) {
                if (item.findElement(By.tagName("i")).getAttribute("class").contains("fa-envelope")) {
                    email = item.findElement(By.className("elementor-icon-list-text")).getText()
                            .replace("[@]", "@").replace("]", "");
                } else if (item.findElement(By.tagName("i")).getAttribute("class").contains("fa-phone-alt")) {
                    phone = item.findElement(By.className("elementor-icon-list-text")).getText();
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.xpath("/html/body/main/div/div[1]/section[1]/div/div[2]/div"));

        String role = this.getRole(div);
        if (role.equals("Invalid Role")) return "Invalid Role";

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", role,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", this.getPracticeArea(div),
                "email", this.getSocials(div)[0],
                "phone", this.getSocials(div)[1]
        );
    }
}
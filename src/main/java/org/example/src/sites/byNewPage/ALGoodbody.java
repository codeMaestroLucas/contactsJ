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

import static java.util.Map.entry;

public class ALGoodbody extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("belfast", "England"),
            entry("dublin", "Ireland"),
            entry("london", "England"),
            entry("new york", "USA"),
            entry("palo alto", "USA"),
            entry("san francisco", "USA")
    );

    private final String[] validRoles = {
            "partner",
            "counsel",
            "senior associate"
    };

    public ALGoodbody() {
        super(
            "AL Goodbody",
            "https://www.algoodbody.com/our-people",
            1,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(ExpectedConditions
                    .presenceOfAllElementsLocatedBy(By.className("card-profile"))
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("h1")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.cssSelector("p")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);

        String role = element.getText();
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = {
                By.cssSelector("p")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String[] split = element.getText().split(",");
        return split[split.length - 1].trim();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("p:nth-child(3)")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String country = siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, element.getText(), "");
        return country.toLowerCase().contains("northen ireland") ? "England" : country;
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


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement((By.className("col-tablet-7")));
        String role = this.getRole(div);

        if (role.equalsIgnoreCase("invalid role")) return "Invalid Role";

        String[] socials = this.getSocials(div);
        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", role,
            "firm", this.name,
            "country", this.getCountry(div),
            "practice_area", this.getPracticeArea(div),
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}

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

public class HoffmannEitle extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("partner")
    };

    public HoffmannEitle() {
        super(
                "Hoffmann Eitle",
                "https://www.hoffmanneitle.com/de/teams?partner=true&team=#form",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        // The URL filter ?partner=true implies the list is already partners,
        // but we will use ByNewPage strategy to filter strictly by role on the detail page
        // or try to detect it here if possible.
        // However, based on instructions, we often filter on listing.
        // The provided listing HTML doesn't explicitly say "Partner",
        // but the detail HTML does (h4.partner).
        // We will collect all items here and let the detail page/filter validate.

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.item.big")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public void openNewTab(WebElement lawyer) {
        WebElement link = lawyer.findElement(By.tagName("a"));
        MyDriver.openNewTab(link.getAttribute("href"));
    }

    private String getName(WebElement container) throws LawyerExceptions {
        By[] byArray = {By.tagName("h1")};
        return extractor.extractLawyerText(container, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement container) throws LawyerExceptions {
        return extractor.extractLawyerText(container, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement container) {
        String email = "";
        String phone = "";
        try {
            List<WebElement> links = container.findElements(By.tagName("a"));
            for (WebElement link : links) {
                String href = link.getAttribute("href");
                if (href != null) {
                    if (href.startsWith("mailto:") && email.isEmpty()) {
                        email = href.replace("mailto:", "").trim();
                    } else if (href.startsWith("tel:") && phone.isEmpty()) {
                        phone = href.replace("tel:", "").trim();
                    }
                }
            }
        } catch (Exception e) {
            // Socials not found
        }
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement mainCell = driver.findElement(By.xpath("/html/body/main/div[1]/div[1]/div[2]"));

        // Check role on the detail page
        String role;
        try {
            role = this.getRole(mainCell);
        } catch (Exception e) {
            return "Invalid Role";
        }

        if (!role.toLowerCase().contains("partner")) {
            return "Invalid Role";
        }

        String[] socials = this.getSocials(mainCell);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(mainCell),
                "role", role,
                "firm", this.name,
                "country", "Germany",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "4989924090" : socials[1]
        );
    }
}
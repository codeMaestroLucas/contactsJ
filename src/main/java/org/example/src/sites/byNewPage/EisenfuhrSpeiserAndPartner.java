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

public class EisenfuhrSpeiserAndPartner extends ByNewPage {
    public EisenfuhrSpeiserAndPartner() {
        super(
                "Eisenfuhr Speiser And Partner",
                "https://www.eisenfuhr.com/en/team",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("lawyer-inner")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public void openNewTab(WebElement lawyer) {
        try {
            WebElement linkElement = lawyer.findElement(By.tagName("a"));
            MyDriver.openNewTab(linkElement.getAttribute("href"));
        } catch (Exception e) {
            System.err.println("Error opening tab: " + e.getMessage());
        }
    }

    private String getName(WebElement divInfo) throws LawyerExceptions {
        By[] byArray = {By.tagName("h2")};
        return extractor.extractLawyerText(divInfo, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement divInfo) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div:nth-of-type(3) > p")};
        String role = extractor.extractLawyerText(divInfo, byArray, "ROLE", LawyerExceptions::roleException);

        String[] validRoles = {"dipl", "advisor", "head", "counsel", "partner", "senior associate", "principal associate"};
        return siteUtl.isValidPosition(role, validRoles) ? role : "Invalid Role";
    }

    private String[] getSocials(WebElement divInfo) {
        String email = "";
        String phone = "";

        try {
            // The contact info is typically in the 4th div
            WebElement contactDiv = divInfo.findElement(By.cssSelector("div:nth-of-type(4)"));
            String text = contactDiv.getText();

            // Simple parsing based on the text content provided
            String[] lines = text.split("\n");
            for (String line : lines) {
                if (line.contains("@")) {
                    email = line.trim();
                } else if (line.toLowerCase().contains("tel")) {
                    phone = line.replace("Tel", "").trim();
                }
            }
        } catch (Exception e) {
            // Socials not found
        }

        return new String[]{email, phone};
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        // The lawyer-info div is the main container on the detail page
        WebElement divInfo = driver.findElement(By.className("lawyer-info"));

        String role = this.getRole(divInfo);
        if ("Invalid Role".equals(role)) {
            return "Invalid Role";
        }

        String[] socials = this.getSocials(divInfo);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(divInfo),
                "role", role,
                "firm", this.name,
                "country", "Germany",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "49895490750" : socials[1]
        );
    }
}
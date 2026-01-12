package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class EldibAdvocates extends ByPage {

    private final By[] byRoleArray = {
            By.className("list-item-content__description")
    };

    public EldibAdvocates() {
        super(
                "Eldib Advocates",
                "https://www.eldibadvocates.com/team",
                1
        );
    }

    private String[] validRoles = {"partner", "counsel", "senior associate"};

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("li.list-item")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.cssSelector("a.list-item-content__button")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("list-item-content__title")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String role = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String[] getSocials(WebElement lawyer, String name) {
        try {
            // (firstName).(lastName)@eldib.com.eg
            name = TreatLawyerParams.treatName(name);
            String cleanName = name.toLowerCase().trim().replace(" ", ".");
            String email = cleanName + "@eldib.com.eg";
            return new String[]{email, ""};
        } catch (Exception e) {
            System.err.println("Error constructing socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String[] socials = this.getSocials(lawyer, name);
        String role = this.getRole(lawyer);
        if (role.equals("Invalid Role")) return "Invalid Role";

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Egypt",
                "practice_area", "",
                "email", socials[0],
                "phone", "2034950000"
        );
    }
}

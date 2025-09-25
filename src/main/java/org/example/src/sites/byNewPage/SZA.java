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

public class SZA extends ByNewPage {

    public SZA() {
        super(
                "SZA",
                "https://www.sza.de/en/professionals",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        // Role is not on the list page, filtering is done on the detail page.
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("person-card")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{By.className("person-card__link")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("h1")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME",  "textContent",LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String[] validRoles = new String[]{"partner", "counsel"};
        By[] byArray = new By[]{
                By.cssSelector("div.intro__contact > p.c4")
        };
        String role = extractor.extractLawyerAttribute(lawyer, byArray, "ROLE",  "textContent",LawyerExceptions::roleException);
        System.out.println(role);
        return siteUtl.isValidPosition(role, validRoles) ? role : "Invalid Role";
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("intro__expertise")
        };
        String practiceArea = extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);
        return practiceArea.split(",")[0].toLowerCase().replace("expertise", "");
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("intro__contact"))
                    .findElements(By.tagName("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("intro--lawyer"));

        String role = this.getRole(div);
        if (role.equals("Invalid Role")) {
            return "Invalid Role";
        }

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", role,
                "firm", this.name,
                "country", "Germany",
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "<I'll fill this if necessary>" : socials[1]
        );
    }
}
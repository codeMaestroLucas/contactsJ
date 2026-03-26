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

public class LambadariosLaw extends ByNewPage {

    public LambadariosLaw() {
        super(
                "Lambadarios Law",
                "https://www.lambadarioslaw.gr/people/",
                1
        );
    }

    By[] byRoleArray = new By[]{
            By.className("lawyer-inner-body"),
            By.cssSelector("p")
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.clickOnAddBtn(By.id("wt-cli-accept-all-btn"));
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("lawyer")
                    )
            );
            return siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.className("readmore")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("panel"),
                By.tagName("h1")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("panel"),
                By.tagName("h2")
        };
       return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer, String name) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("quick-links"))
                    .findElements(By.tagName("a"));
            String[] socialsToReturn = super.getSocials(socials, false);

            // create email: first letter of first name + last name + @lambadarioslaw.gr
            String[] parts = name.toLowerCase().trim().split("\\s+");
            if (parts.length >= 2) {
                String firstName = parts[0];
                String lastName = parts[parts.length - 1];
                String email = firstName.charAt(0) + "." + lastName + "@lambadarioslaw.gr";
                socialsToReturn[0] = email;
            } else {
                socialsToReturn[0] = ""; // fallback if name doesn't have at least 2 parts
            }

            return socialsToReturn;
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("container"));

        String name = this.getName(div);

        String[] socials = this.getSocials(div, name);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Greece",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "302103224047" : socials[1]
        );
    }
}
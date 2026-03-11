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

public class SchalastAndPartner extends ByNewPage {

    public SchalastAndPartner() {
        super(
                "SchalastAndPartner",
                "https://www.schalast.com/en/team/all_attorneys.php",
                1
        );
    }

    private final String[] validRoles = {"partner", "founder", "head", "director", "counsel", "senior associate"};

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("app")));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement header = driver.findElement(By.tagName("header"));

        String name = extractor.extractLawyerText(header, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
        String role = this.getRole(header);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(header);


        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Germany",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }

    private String getRole(WebElement header) {
        String role = header.findElement(By.tagName("h2")).getAttribute("textContent");
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }
}

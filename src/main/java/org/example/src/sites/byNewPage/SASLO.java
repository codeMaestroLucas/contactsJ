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

public class SASLO extends ByNewPage {

    public SASLO() {
        super(
                "SASLO",
                "https://www.saslo.com/our-lawyers",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("lawyer-item")));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("lawyer-link-block")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("contact-wrapper")).findElements(By.tagName("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("lawyer-wrapper"));

        String role = this.getRole(container);

        String name = extractor.extractLawyerText(container, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Oman",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }

    private String getRole(WebElement container) {
        String role = container.findElement(By.xpath("/html/body/div[3]/div/div/div/div[2]/div[1]/div[1]/p[1]/strong")).getAttribute("textContent");
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }
}

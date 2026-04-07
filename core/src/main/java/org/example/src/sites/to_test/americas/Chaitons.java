package org.example.src.sites.to_test.americas;

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

public class Chaitons extends ByNewPage {

    public Chaitons() {
        super(
                "Chaitons",
                "https://www.chaitons.com/lawyers",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("teamMember")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileLink = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h3 a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(profileLink);
        return profileLink;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("bioContact"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("position")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String firstName = extractor.extractLawyerText(container, new By[]{By.className("firstName")}, "NAME", LawyerExceptions::nameException);
        String lastName = extractor.extractLawyerText(container, new By[]{By.className("lastName")}, "NAME", LawyerExceptions::nameException);

        List<WebElement> socialElements = container.findElements(By.cssSelector(".phoneFaxEmailWrapper a"));
        String[] socials = super.getSocials(socialElements, false);

        return Map.of(
                "link", driver.getCurrentUrl(),
                "name", firstName + " " + lastName,
                "role", role,
                "firm", this.name,
                "country", "Canada",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "4162181160" : socials[1]
        );
    }
}

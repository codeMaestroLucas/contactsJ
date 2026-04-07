package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class RoblesMiaja extends ByNewPage {

    public RoblesMiaja() {
        super(
                "Robles Miaja",
                "https://www.roblesmiaja.com/our-team",
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
            return MyDriver.wait.findElements(By.cssSelector("[id^='comp-mgsh42i3']"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String email = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("a[href^='mailto:']")}, "EMAIL", LawyerExceptions::emailException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.cssSelector("[id^='comp-mgtuap007']"));
        String role = extractor.extractLawyerText(container, new By[]{By.cssSelector("h1:last-child")}, "ROLE", LawyerExceptions::roleException);

        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        if (!validPosition) return "Invalid Role";

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", "",
                "email", email,
                "phone", "525511030000"
        );
    }
}

package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Quorus extends ByPage {

    public Quorus() {
        super(
                "Quorus",
                "https://quorus.ch/team",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("team-card"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("role")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("role")}, "ROLE", LawyerExceptions::roleException);

        // Click to open modal
        lawyer.click();
        WebElement modal = MyDriver.wait.findElement(By.className("modal-content"));

        String[] socials = this.getSocials(modal.findElements(By.cssSelector("a[href^='mailto:']")), true);
        String practiceArea = extractor.extractLawyerText(modal, new By[]{By.className("list")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        // Close modal for next iteration
        try {
            MyDriver.wait.findElement(By.className("close")).click();
        } catch (Exception ignored) {}

        return Map.of(
                "link", this.link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Switzerland",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "41445124111" : socials[1]
        );
    }
}

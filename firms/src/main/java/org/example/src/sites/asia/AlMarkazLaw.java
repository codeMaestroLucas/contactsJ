package org.example.src.sites.asia;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class AlMarkazLaw extends ByPage {

    public AlMarkazLaw() {
        super(
                "Al Markaz Law",
                "https://www.markazlaw.com/Litigation-Team.html",
                2
        );
    }
    String currentRole = "";
    String currentPA = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.markazlaw.com/Corporate-Advisory-Team.html";
        String url = index == 0 ? this.link : otherUrl;
        currentRole = index == 0 ? "Partner" : "Advisor";
        currentPA = index == 0 ? "" : "Corporate Advisory";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".col-sm-6.col-lg-4")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h5")}, "NAME", LawyerExceptions::nameException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("link2")}, "LINK", "href", LawyerExceptions::linkException);
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", currentRole,
                "firm", this.name,
                "country", "Kuwait",
                "practice_area", currentPA,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "96522464640" : socials[1]
        );
    }
}

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

public class Skrine extends ByNewPage {

    public Skrine() {
        super(
                "Skrine",
                "https://www.skrine.com/people/partners#people-partners",
                6
        );
    }

    String currentRole = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String url = "";
        if (index < 5) {
            url = "https://www.skrine.com/people/partners?page=" + (index + 1) + "#people-partners";
            currentRole = "Partner";
        } else {
            url =  "https://www.skrine.com/people/senior-associates?page=" + (index - 4) + "#people-partners";
            currentRole = "Senior Associate";
        }
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("dt.col-md-4.col-sm-6")));
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
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("shortProfile"));
        String name = extractor.extractLawyerText(container, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException).split("\n")[0].trim();
        String contactBox = extractor.extractLawyerText(container, new By[]{By.className("contact-det")}, "CONTACT BOX", (e) -> null);
        String[] socials = super.getSocialsFromText(contactBox);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", currentRole,
                "firm", this.name,
                "country", "Malaysia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "60320813999" : socials[1]
        );
    }
}

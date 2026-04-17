package org.example.src.sites.europe;

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

public class Orsingher extends ByNewPage {

    public Orsingher() {
        super(
                "Orsingher",
                "https://orsingher.com/en/professionals/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.id("category-senior-partner"))
            );

            List<WebElement> lawyers = div.findElements(By.className("Person"));

            div = driver.findElement(By.id("category-partner"));
            lawyers.addAll(div.findElements(By.className("Person")));

            div = driver.findElement(By.id("category-of-counsel"));
            lawyers.addAll(div.findElements(By.className("Person")));

            div = driver.findElement(By.id("category-counsel"));
            lawyers.addAll(div.findElements(By.className("Person")));

            div = driver.findElement(By.id("category-managing-associate"));
            lawyers.addAll(div.findElements(By.className("Person")));

            div = driver.findElement(By.id("category-senior-associate"));
            lawyers.addAll(div.findElements(By.className("Person")));

            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("Person_Image")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement container) throws LawyerExceptions {
        return extractor.extractLawyerText(container, new By[]{By.className("PageProfessionista_Nome")}, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement container) throws LawyerExceptions {
        return extractor.extractLawyerText(container, new By[]{By.tagName("h2")}, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement container) {
        try {
            WebElement emailA = container.findElement(By.className("PageProfessionista_ContactBtn"));
            return new String[]{emailA.getAttribute("href"), ""};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("PageProfessionista"));
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", this.getRole(container),
                "firm", this.name,
                "country", "Italy",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "39028907501" : socials[1]
        );
    }
}

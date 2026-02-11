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

public class ArethaLegal extends ByNewPage {

    private final By[] byRoleArray = {
            By.className("team-position")
    };

    public ArethaLegal() {
        super(
                "Aretha Legal",
                "https://www.arethalegal.com/our-team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnAddBtn(By.className("disclaimer-accept"));
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"team-results\"]/div[1]")
                    )
            );
            List<WebElement> lawyers = div.findElements(By.className("team-card"));

            div = driver.findElement(By.xpath("//*[@id=\"team-results\"]/div[2]"));
            lawyers.addAll(div.findElements(By.className("team-card")));

            div = driver.findElement(By.xpath("//*[@id=\"team-results\"]/div[3]"));
            lawyers.addAll(div.findElements(By.className("team-card")));

            div = driver.findElement(By.xpath("//*[@id=\"team-results\"]/div[5]"));
            lawyers.addAll(div.findElements(By.className("team-card")));

            lawyers.remove(1);
            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("main-title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("team-position")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a[href^='mailto:'], a[href^='tel:']"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(
                By.xpath("//*[starts-with(@id,'post-')]/div/div/div/section[1]/div/div/div/section/div/div[1]/div")
        );

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", this.getRole(container),
                "firm", this.name,
                "country", "India",
                "practice_area", "",
                "email", socials[0],
                "phone", "911143561665"
        );
    }
}

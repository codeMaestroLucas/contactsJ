package org.example.src.sites.byPage;

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

public class ONeillAndBorges extends ByPage {

    public ONeillAndBorges() {
        super(
                "O'Neill & Borges",
                "https://www.oneillborges.com/our-attorneys/#alpha",
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
                    ExpectedConditions.presenceOfElementLocated(By.id("attorneys-section-capital-member"))
            );

            List<WebElement> lawyers = div.findElements(By.className("filter-card"));

            div = driver.findElement(By.id("attorneys-section-income-member"));
            lawyers.addAll(div.findElements(By.className("filter-card")));

            div = driver.findElement(By.id("attorneys-section-counsel"));
            lawyers.addAll(div.findElements(By.className("filter-card")));

            div = driver.findElement(By.id("attorneys-section-senior-associate"));
            lawyers.addAll(div.findElements(By.className("filter-card")));

            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, new By[]{By.className("bio-link-wrap")}, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> links = lawyer.findElements(By.tagName("a"));
            return super.getSocials(links, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "----",
                "firm", this.name,
                "country", "USA",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "7877648181" : socials[1]
        );
    }
}

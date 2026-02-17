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

public class AllenAndGledhill extends ByPage {

    public AllenAndGledhill() {
        super(
                "Allen & Gledhill",
                "https://www.allenandgledhill.com/partners/",
                19,
                2
        );
    }

    @Override
    protected void accessPage(int index) {
        String otherUrl = "https://www.allenandgledhill.com/partners/?page="+ (index + 1) + "&";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("article.card--profile")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a")};
        String link = extractor.extractLawyerAttribute(lawyer.findElement(By.className("card-media")), byArray, "LINK", "href", LawyerExceptions::linkException);
        return link.startsWith("http") ? link : "https://www.allenandgledhill.com" + link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.h3.heading a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("p")};
        return extractor.extractLawyerText(lawyer.findElement(By.className("card-body")), byArray, "COUNTRY", LawyerExceptions::countryException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.cssSelector("div.email a")).getAttribute("href");
        } catch (Exception ignored) {}
        try {
            phone = lawyer.findElement(By.cssSelector("div.tel")).getText();
        } catch (Exception ignored) {}
        return new String[]{email, phone};
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "",
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
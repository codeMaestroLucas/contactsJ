package org.example.src.sites._standingBy.toAvoidForNow.americas;

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

public class RomeroPineda extends ByPage {
    public RomeroPineda() {
        super(
                "Romero Pineda",
                "https://www.romeropineda.com/en/nuestra-firma/#nuestro-equipo",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement partnersDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"nuestro-equipo\"]/div/div/div/div[2]/div/div/div")));
            List<WebElement> lawyers = partnersDiv.findElements(By.className("qodef-e-content"));

            WebElement managersDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"qodef-page-content\"]/div/div/div/section[5]")));
            lawyers.addAll(managersDiv.findElements(By.className("qodef-e-content")));

            WebElement seniorsDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"qodef-page-content\"]/div/div/div/section[6]")));
            lawyers.addAll(seniorsDiv.findElements(By.className("qodef-e-content")));

            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h5.qodef-e-title > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h5.qodef-e-title > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        try {
            email = lawyer.findElement(By.className("qodef-e-degree")).getAttribute("textContent").trim();
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{email, ""};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "",
                "firm", this.name,
                "country", "El Salvador",
                "practice_area", "",
                "email", socials[0],
                "phone", "50325055555"
        );
    }
}
package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class KojimaLaw extends ByPage {

    public KojimaLaw() {
        super(
                "Kojima Law",
                "https://www.kojimalaw.jp/en/profile/#of_counsel",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"side-scroll-area\"]/div[2]")));
            List<WebElement> lawyers = div.findElements(By.className("list-elm"));

            div = driver.findElement(By.xpath("//*[@id=\"side-scroll-area\"]/div[4]"));
            lawyers.addAll(div.findElements(By.className("list-elm")));

            div = driver.findElement(By.xpath("//*[@id=\"side-scroll-area\"]/div[6]"));
            lawyers.addAll(div.findElements(By.className("list-elm")));

            div = driver.findElement(By.xpath("//*[@id=\"side-scroll-area\"]/div[12]"));
            lawyers.addAll(div.findElements(By.className("list-elm")));

            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find profile list", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getEmail(String name) {
        String[] parts = TreatLawyerParams.treatNameForEmail(name).split(" ");
        return parts[parts.length - 1] + "@kojimalaw.jp";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String email = this.getEmail(name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", "---",
                "firm", this.name,
                "country", "Japan",
                "practice_area", "",
                "email", email,
                "phone", "81332221401"
        );
    }
}
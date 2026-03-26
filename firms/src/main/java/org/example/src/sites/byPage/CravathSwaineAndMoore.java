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

public class CravathSwaineAndMoore extends ByPage {

    public CravathSwaineAndMoore() {
        super(
                "Cravath, Swaine & Moore",
                "https://www.cravath.com/people/index.html",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.clickOnElement(By.xpath("//*[@id=\"app\"]/div[1]/div[2]/div/section/div/div/div[2]/button[1]"));
        MyDriver.scrollToBottom(0.3);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"app\"]/div[1]/div[4]/div/div[2]/div[2]/div[1]"))
            );

            List<WebElement> lawyers = div.findElements(By.cssSelector("div[class*='styles__personCard']"));

            div = driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[4]/div/div[2]/div[2]/div[2]"));
            lawyers.addAll(div.findElements(By.cssSelector("div[class*='styles__personCard']")));

            div = driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div[4]/div/div[2]/div[2]/div[3]"));
            lawyers.addAll(div.findElements(By.cssSelector("div[class*='styles__personCard']")));

            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".type__h3 a")}, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".type__h3 a")}, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("a"));
            return super.getSocials(socials, false);
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
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "12124741000" : socials[1]
        );
    }

    private String getCountry(WebElement lawyer) {
        String country = lawyer.findElement(By.cssSelector("li > a[href*='/locations/']")).getAttribute("textContent");
        assert country != null;
        return country.equalsIgnoreCase("london") ? "England" : "USA";
    }
}

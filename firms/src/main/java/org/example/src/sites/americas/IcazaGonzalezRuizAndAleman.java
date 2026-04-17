package org.example.src.sites.americas;

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

public class IcazaGonzalezRuizAndAleman extends ByNewPage {

    public IcazaGonzalezRuizAndAleman() {
        super(
                "Icaza González-Ruiz & Alemán",
                "https://icazalaw.com/our-experts/",
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
        try {


            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"partners\"]")));
            return div.findElements(By.cssSelector("a[href*='https://icazalaw.com/our-experts']"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return link;
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("p"));
            return super.getSocials(socials, true);

        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {

        this.openNewTab(lawyer);
        WebElement header = driver.findElement(By.xpath("//*[@id=\"Content\"]/div/main/div/div/section[1]/div[2]/div/div/div[2]/div/div"));
        WebElement container = driver.findElement(By.xpath("//*[@id=\"Content\"]/div/main/div/div/section[2]/div[2]/div[1]/div/div[3]/div/div"));

        String[] socials = this.getSocials(container);


        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerAttribute(header, new By[]{By.tagName("h2")}, "NAME", "textContent", LawyerExceptions::nameException),
                "role", extractor.extractLawyerAttribute(header, new By[]{By.tagName("h4")}, "ROLE", "textContent", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Panama",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "5072056000" : socials[1]
        );
    }
}

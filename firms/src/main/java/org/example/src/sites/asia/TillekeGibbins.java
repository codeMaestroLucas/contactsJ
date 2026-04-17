package org.example.src.sites.asia;

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

public class TillekeGibbins extends ByNewPage {

    public TillekeGibbins() {
        super(
                "Tilleke & Gibbins",
                "https://www.tilleke.com/professionals/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.rollDown(60, 0.8);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            List<WebElement> lawyers = div.findElements(By.cssSelector("section.elementor-top-section"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("div")}, false);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h2.elementor-heading-title a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement panel = driver.findElement(By.xpath("/html/body/div/section[1]/div[2]/div[2]/div"));

        String name = driver.findElement(By.xpath("/html/body/div/section[1]/div[2]/div[2]/div/section/div/div/div/div[2]/div/h2")).getAttribute("textContent");
        String role = extractor.extractLawyerText(panel, new By[]{By.cssSelector("h5.elementor-heading-title")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocials(panel.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Thailand",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "6620565600" : socials[1]
        );
    }
}

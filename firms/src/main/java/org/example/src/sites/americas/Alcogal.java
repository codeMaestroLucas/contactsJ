package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Alcogal extends ByNewPage {

    public Alcogal() {
        super(
                "Alcogal",
                "https://alcogal.com/lawyers/",
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

            wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"content\"]/div/div/section[3]"))
            );

            WebElement div;
            List<WebElement> lawyers = new ArrayList<>();

            for (int i = 3; i < 12; i++) {
                String xpath = "//*[@id=\"content\"]/div/div/section[" + i + "]";
                div = driver.findElement(By.xpath(xpath));

                lawyers.addAll(div.findElements(
                        By.cssSelector("div[data-element_type='column'][data-e-type='column']")
                ));
            }

            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("h6")}, true);
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
        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//div/div/section[2]/div/div[1]/div/section/div/div[2]/div"));

        String name = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("//div/div/section[2]/div/div[1]/div/section/div/div[2]/div/div[3]/div/div/div[2]/h3/span")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocials(container.findElements(By.tagName("span")), true);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Panama",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "5072692620" : socials[1]
        );
    }
}

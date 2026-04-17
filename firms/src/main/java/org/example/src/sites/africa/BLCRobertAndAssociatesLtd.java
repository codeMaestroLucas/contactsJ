package org.example.src.sites.africa;

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

public class BLCRobertAndAssociatesLtd extends ByNewPage {

    public BLCRobertAndAssociatesLtd() {
        super(
                "BLC Robert & Associates Ltd",
                "https://www.blc.mu/team-members/",
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
            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"content\"]/div/div/section[2]/div/div/div/div[3]/div/div/div"))
            );

            List<WebElement> lawyers = div.findElements(By.className("jet-listing-grid__item"));

            div = driver.findElement(By.xpath("//*[@id=\"content\"]/div/div/section[2]/div/div/div/div[5]/div/div/div"));
            lawyers.addAll(div.findElements(By.className("jet-listing-grid__item")));

            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = lawyer.getAttribute("textContent").replace("SA", "").trim();

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//*[@id=\"main\"]/div/section[3]/div/div/div/section/div/div[1]/div"));
        List<WebElement> h5 = container.findElements(By.tagName("h5"));
        String[] socials = super.getSocials(h5, true);


        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", "----",
                "firm", this.name,
                "country", "Mauritius",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "2304032400" : socials[1]
        );
    }
}

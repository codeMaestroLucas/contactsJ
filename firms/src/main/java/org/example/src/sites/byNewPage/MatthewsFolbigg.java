package org.example.src.sites.byNewPage;

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
import java.util.Objects;

public class MatthewsFolbigg extends ByNewPage {

    private final By[] byRoleArray = {By.cssSelector("h6 > a")};

    public MatthewsFolbigg() {
        super(
                "Matthews Folbigg",
                "https://www.matthewsfolbigg.com.au/our-people/",
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
        String[] validRoles = {"principal", "director", "partner", "counsel", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> divs = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.entry-content div.container")));

            divs.removeFirst();

            List<WebElement> lawyers = new ArrayList<>();
            for (WebElement div : divs) {
                lawyers.addAll(div.findElements(By.cssSelector(".vc_column_container > .vc_column-inner > div.wpb_wrapper")));
            }
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyers", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".vc_custom_heading a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".vc_custom_heading a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h5")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String textContent = lawyer.findElement(By.xpath("//*[starts-with(@id,'post-')]//div[3]/div[2]/div/div[1]/div/div/div[2]/div")).getAttribute("textContent");
            return super.getSocialsFromText(textContent);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        this.openNewTab(lawyer);

        WebElement bioWrapper = driver.findElement(By.xpath("//*[starts-with(@id,'post-')]//div[3]/div[2]/div/div[1]/div/div"));
        String[] socials = this.getSocials(bioWrapper);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", this.getRole(bioWrapper),
                "firm", this.name,
                "country", "Australia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "296357966" : socials[1]
        );
    }
}
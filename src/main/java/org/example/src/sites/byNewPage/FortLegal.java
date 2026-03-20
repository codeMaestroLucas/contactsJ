package org.example.src.sites.byNewPage;

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

public class FortLegal extends ByNewPage {

    public FortLegal() {
        super(
                "Fort Legal",
                "https://fortlegal.com/en/lawyers/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div#lawyersTeamMobile > div.h-full.w-full")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("p.text-xs")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href^='/en/lawyer/']")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
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
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("p.font-bold")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("p.text-xs")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//*[@id=\"mainBody\"]/div/section[2]/div[2]"));
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Lithuania",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.cssSelector("li.py-5 a")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "37052506141" : socials[1]
        );
    }
}

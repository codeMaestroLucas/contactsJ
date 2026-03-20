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

public class FTPA extends ByNewPage {

    public FTPA() {
        super(
                "FTPA",
                "https://ftpa.com/en/equipe/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("new-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".infos span")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
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
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("span")}, "ROLE", "textContent", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("left-side"));
        String[] socials = this.getSocials(container);


        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "France",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "330145008620" : socials[1]
        );
    }
}

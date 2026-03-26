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

public class AbrahamsAndGrossIncorporated extends ByNewPage {

    public AbrahamsAndGrossIncorporated() {
        super(
                "Abrahams & Gross Incorporated",
                "https://www.abgross.co.za/team/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("et_pb_column_1_3")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("div")}, false);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
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
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("p:not(:has(a))")}, "ROLE", "textContent", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement container = null;
        try {
        container = driver.findElement(By.xpath("//div/div/div/div[2]/div/div[1]/div/div/div[2]/div"));

        } catch (Exception e) {
            container = driver.findElement(By.xpath("//div/div/div/div[1]/div/div[1]/div/div/div[1]"));
        }

        String[] socials = this.getSocials(container);


        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "South Africa",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "270214221323" : socials[1]
        );
    }
}

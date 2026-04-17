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

public class CollyerLaw extends ByNewPage {

    public CollyerLaw() {
        super(
                "Collyer Law",
                "https://www.collyerlaw.com/team",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("TmK0x")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("div")}, false);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='https://www.collyerlaw.com/team/']")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);

        String name = driver.findElement(By.xpath("//h4/span/span/span/span")).getAttribute("textContent");
        String role = driver.findElement(By.xpath("//p/span")).getAttribute("textContent");
        String mail = driver.findElement(By.xpath("//p[1]/span/a")).getAttribute("textContent");
        String phone = driver.findElement(By.xpath("//p[2]/span")).getAttribute("textContent");

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Singapore",
                "practice_area", "",
                "email", mail,
                "phone", phone.isEmpty() ? "6569502875" : phone
        );
    }
}

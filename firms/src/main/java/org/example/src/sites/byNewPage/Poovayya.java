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

public class Poovayya extends ByNewPage {

    public Poovayya() {
        super(
                "Poovayya",
                "https://www.poovayya.net/our-people/",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("listingdata-col")));
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
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("title")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        String email = extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[]{By.xpath("/html/body/div[2]/div[3]/div/div[2]/div[2]/ul/li[1]/span[2]")}, "EMAIL", "textContent", LawyerExceptions::emailException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "---",
                "firm", this.name,
                "country", "India",
                "practice_area", "",
                "email", email,
                "phone", "918022213707"
        );
    }
}

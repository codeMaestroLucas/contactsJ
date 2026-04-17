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

public class GuzmanAriza extends ByNewPage {

    public GuzmanAriza() {
        super(
                "Guzmán Ariza",
                "https://drlawyer.com/the-firm-2/#pro",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.boxes a.more[href*='https://drlawyer.com/attorney/']")));

            MyDriver.clickOnElement(By.xpath("//*[@id=\"equipo\"]/div/nav/ul/li[3]/a"));
            Thread.sleep(1500);
            lawyers.addAll(driver.findElements(By.cssSelector("div.boxes a.more[href*='https://drlawyer.com/attorney/']")));

            MyDriver.clickOnElement(By.xpath("//*[@id=\"equipo\"]/div/nav/ul/li[5]/a"));
            Thread.sleep(1500);
            lawyers.addAll(driver.findElements(By.cssSelector("div.boxes a.more[href*='https://drlawyer.com/attorney/']")));

            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement header = driver.findElement(By.xpath("//*[@id=\"page-content\"]/div[2]/div"));

        String name = extractor.extractLawyerText(header, new By[]{By.id("name-v")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(header, new By[]{By.id("job-v")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerAttribute(header, new By[]{By.id("email-v")}, "EMAIL", "href", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(header, new By[]{By.id("tel-v")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Dominican Republic",
                "practice_area", extractor.extractLawyerText(header, new By[]{By.xpath("//h3[contains(.,'Practice Areas')]/following-sibling::ul")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", email,
                "phone", phone.isEmpty() ? "8092550980" : phone
        );
    }
}

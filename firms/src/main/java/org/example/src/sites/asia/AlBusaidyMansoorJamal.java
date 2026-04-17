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

public class AlBusaidyMansoorJamal extends ByNewPage {

    public AlBusaidyMansoorJamal() {
        super(
                "Al Busaidy, Mansoor Jamal & Co",
                "https://www.amjoman.com/people/",
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
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"slider2\"]/dd[1]/span/ul"))
            );
            List<WebElement> lawyers = div.findElements(By.cssSelector("a[href*='https://www.amjoman.com/']"));

            div = driver.findElement(By.xpath("//*[@id=\"slider2\"]/dd[2]/span/ul"));
            lawyers.addAll(div.findElements(By.cssSelector("a[href*='https://www.amjoman.com/']")));

            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return null;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("right-side"));

        String name = extractor.extractLawyerText(container, new By[]{By.cssSelector("div.name-box p.name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.cssSelector("div.name-box p.managing")}, "ROLE", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerText(driver.findElement(By.className("practicebox")), new By[]{By.className("lawyers-right")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String[] socials = super.getSocials(container.findElements(By.cssSelector(".contact-box div.call, .mail-box a")), true);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Oman",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "96824829200" : socials[1]
        );
    }
}

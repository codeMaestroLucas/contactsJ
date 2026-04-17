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

public class EnglingStritterAndPartners extends ByNewPage {

    public EnglingStritterAndPartners() {
        super(
                "Engling Stritter & Partners",
                "https://www.englinglaw.com.na/our-team/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("wixui-repeater__item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("h2.font_8")}, true);
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

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h2.font_2")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h2.font_8")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.tagName("main"));
        List<WebElement> p = container.findElements(By.tagName("p"));
        String[] socials = super.getSocials(p, true);
        String[] parts = socials[0].split("email:");

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role.replace("Position:", ""),
                "firm", this.name,
                "country", "Namibia",
                "practice_area", "",
                "email", parts[1],
                "phone", parts[0].isEmpty() ? "26461383300" : parts[0]
        );
    }
}
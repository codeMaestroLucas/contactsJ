package org.example.src.sites.to_test;

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

public class LivingstonAlexanderAndLevy extends ByNewPage {

    public LivingstonAlexanderAndLevy() {
        super(
                "Livingston Alexander & Levy",
                "https://www.lival.co/attorneys",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("addon-root-image")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement subPage) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".addon-root-heading h2")};
        return extractor.extractLawyerText(subPage, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement subPage) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".addon-root-heading h3")};
        return extractor.extractLawyerText(subPage, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement subPage) {
        try {
            List<WebElement> socials = subPage.findElements(By.tagName("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("sppb-row"));
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", this.getRole(container),
                "firm", this.name,
                "country", "Jamaica",
                "practice_area", "",
                "email", socials[0],
                "phone", "18769226310"
        );
    }
}

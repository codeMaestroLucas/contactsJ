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

public class NunesScholefieldDeLeonAndCo extends ByNewPage {

    public NunesScholefieldDeLeonAndCo() {
        super(
                "Nunes Scholefield DeLeon & Co",
                "https://nsdco.com/attorneys/",
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
        String[] validRoles = {"partner", "counsel", "senior associate"};
        By roleBy = By.cssSelector(".elementor-element-1cdcf0b p");

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div[data-elementor-type='loop-item']"))
            );
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{roleBy}, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3 a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement container) throws LawyerExceptions {
        return extractor.extractLawyerText(container, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement container) throws LawyerExceptions {
        return driver.findElement(By.xpath("//*[@id=\"main\"]/div/div[1]/div/div[2]/div/p")).getAttribute("textContent");
    }

    private String[] getSocials() {
        try {
            WebElement container = driver.findElement(By.xpath("//*[@id=\"main\"]/div/div[2]/div/div[1]/div[1]"));
            List<WebElement> socials = driver.findElements(By.tagName("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("elementor-widget-container"));
        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", this.getRole(container),
                "firm", this.name,
                "country", "Jamaica",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "8769608985" : socials[1]
        );
    }
}

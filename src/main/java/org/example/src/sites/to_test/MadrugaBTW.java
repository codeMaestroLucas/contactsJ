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

public class MadrugaBTW extends ByNewPage {

    public MadrugaBTW() {
        super(
                "Madruga BTW",
                "https://madruga.com/en/team/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("bckg-soc")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.xpath(".//h2[contains(text(),'PARTNER')]")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.elementor-heading-title a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        String name = extractor.extractLawyerText(null, new By[]{By.cssSelector("h1.elementor-heading-title")}, "NAME", LawyerExceptions::nameException);

        WebElement socialsContainer = driver.findElement(By.xpath("//div/div/div[2]/div/div[1]/div[4]"));
        String email = extractor.extractLawyerText(socialsContainer, new By[]{By.tagName("a")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", email,
                "phone", "551130450520"
        );
    }
}

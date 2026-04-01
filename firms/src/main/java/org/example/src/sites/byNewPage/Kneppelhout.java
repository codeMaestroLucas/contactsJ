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

public class Kneppelhout extends ByNewPage {

    public Kneppelhout() {
        super(
                "Kneppelhout",
                "https://kneppelhout.com/about-us/our-people/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("pt-cv-content-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("terms")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a.pt-cv-href-thumbnail")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String[] getSocials(WebElement lawyer) throws LawyerExceptions {
        String email = extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//p[contains(., 'Email')]")}, "EMAIL", LawyerExceptions::phoneException).replace("Email", "");
        String phone = extractor.extractLawyerAttribute(lawyer, new By[]{By.xpath(".//p[contains(., 'Phone')]")}, "PHONE", "textContent", LawyerExceptions::phoneException);
        return new String[]{email, phone};
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("gdlr-core-pbf-column-content"));

        String name = extractor.extractLawyerText(container, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.className("gdlr-core-skin-caption")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "310104005100" : socials[1]
        );
    }
}

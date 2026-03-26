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

public class CC extends ByNewPage {

    public CC() {
        super(
                "C&C",
                "https://www.ccadvog.com/cca/our-team?law=140&areas=&ind=&lan=",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("bf-lawyers-box")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("bf-lawyers-name")}, true);
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
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".bf-lawyers-name p")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement contactBox = driver.findElement(By.className("peo-contact"));

        String name = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.className("peo-name")}, "NAME", LawyerExceptions::nameException);
        String[] socials = super.getSocials(contactBox.findElements(By.tagName("a")), false);
        String phone = super.getSocialsFromText(contactBox.getText())[1];

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Macau",
                "practice_area", "",
                "email", socials[0],
                "phone", phone.isEmpty() ? "85328372623" : phone
        );
    }
}

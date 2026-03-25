package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class BARGERPREKOP extends ByPage {

    public BARGERPREKOP() {
        super(
                "BARGER PREKOP",
                "https://www.bargerprekop.com/the-team/partners/",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id("article")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        By[] nameBy = {By.cssSelector("h1.sub")};
        By[] emailBy = {By.cssSelector("a[href^='mailto:']")};
        By[] phoneBy = {By.xpath(".//th[contains(text(),'phone:')]/following-sibling::td")};

        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector("a[href^='mailto:'], a[href^='tel:']")), false);
        String phoneExtract = extractor.extractLawyerText(lawyer, phoneBy, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", driver.getCurrentUrl(),
                "name", extractor.extractLawyerText(lawyer, nameBy, "NAME", LawyerExceptions::nameException),
                "role", "Partner",
                "firm", this.name,
                "country", "Slovakia",
                "practice_area", "",
                "email", extractor.extractLawyerText(lawyer, emailBy, "EMAIL", LawyerExceptions::emailException),
                "phone", phoneExtract.isEmpty() ? "+421 2 3211 9890" : phoneExtract
        );
    }
}

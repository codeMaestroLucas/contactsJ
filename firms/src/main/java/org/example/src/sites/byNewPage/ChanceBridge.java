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

public class ChanceBridge extends ByNewPage {

    public ChanceBridge() {
        super(
                "Chance Bridge",
                "https://chancebridge.com/en/zyry/",
                10
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
        } else {
            MyDriver.clickOnElement(By.xpath("/html/body/div[2]/div[3]/div[5]/a[5]"));
        }
        Thread.sleep(1000);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".wow.fadeInUp")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("subname")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileLink = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(profileLink);
        return profileLink;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("name")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("subname")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String pa = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("viw")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);

        this.openNewTab(lawyer);
        String[] socials = super.getSocialsFromText(driver.findElement(By.className("person-link-type")).getText());

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "China",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "861085419666" : socials[1]
        );
    }
}

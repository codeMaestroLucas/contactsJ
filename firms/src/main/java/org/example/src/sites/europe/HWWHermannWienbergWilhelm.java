package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HWWHermannWienbergWilhelm extends ByNewPage {

    public HWWHermannWienbergWilhelm() {
        super(
                "hww hermann wienberg wilhelm",
                "https://www.hww.eu/en/about-us/team",
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
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-list__item")));
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h6")}, "NAME", LawyerExceptions::nameException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("team-detail__layout"));

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", Objects.requireNonNull(driver.findElement(By.xpath("//div/section/div/div[3]/div/p")).getAttribute("textContent")),
                "firm", this.name,
                "country", "Germany",
                "practice_area", "",
                "email", TreatLawyerParams.treatNameForEmail(name) + "@hww.eu",
                "phone", "4940899560"
        );
    }
}

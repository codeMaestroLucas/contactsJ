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

public class GilibertiTriscorniaEAssociati extends ByNewPage {

    public GilibertiTriscorniaEAssociati() {
        super(
                "Giliberti Triscornia e Associati",
                "https://www.gtalex.com/en/professionisti/partners/",
                2
        );
    }

    private String currentRole = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.gtalex.com/en/professionisti/the-counsels/";
        String url = index == 0 ? this.link : otherUrl;
        currentRole = index == 0 ? "Partner" : "Counsel";

        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("col-associates")));
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
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("associate-name")}, "NAME", "textContent", LawyerExceptions::nameException);

        this.openNewTab(lawyer);
        String email = driver.findElement(By.cssSelector(".associate-email a")).getAttribute("href");

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", currentRole,
                "firm", this.name,
                "country", "Italy",
                "practice_area", "",
                "email", email,
                "phone", "3902880061"
        );
    }
}

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

public class SchellenbergWittmer extends ByNewPage {

    public SchellenbergWittmer() {
        super(
                "Schellenberg Wittmer",
                "https://www.swlegal.com/en/lawyers/",
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
        String[] validRoles = {"partner", "counsel", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.box-inner")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("function")}, false, validRoles);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement panel = driver.findElement(By.className("panel-inner"));

        String name = extractor.extractLawyerText(panel, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(panel, new By[]{By.className("function")}, "ROLE", LawyerExceptions::roleException);

        String[] socials = this.getSocials(panel);


        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Switzerland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "41442155252" : socials[1]
        );
    }
}

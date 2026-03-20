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

public class LenzAndStaehelin extends ByNewPage {

    public LenzAndStaehelin() {
        super(
                "Lenz & Staehelin",
                "https://www.lenzstaehelin.com/people-across-the-firm/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("people-list_item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("people-list_item_details")}, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("people-list_name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement subPage) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".header-image__title span")};
        String role = extractor.extractLawyerText(subPage, byArray, "ROLE", LawyerExceptions::roleException);
        return role.split("\n")[0].replace(",", "").trim();
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
        String name = this.getName(lawyer);
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.id("c243"));
        String role = this.getRole(container);
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Switzerland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "41584508000" : socials[1]
        );
    }
}

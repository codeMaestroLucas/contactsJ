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

public class CCALegal extends ByNewPage {

    public CCALegal() {
        super(
                "CCA",
                "https://www.cca.law/en/team/?all",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.equipa-list > a.card")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("card-subtitle")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileLink = lawyer.getAttribute("href");
        MyDriver.openNewTab(profileLink);
        return profileLink;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("card-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("card-subtitle")}, "ROLE", LawyerExceptions::roleException);


        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//footer/div[1]/div[3]/div[2]/div/div[2]"));


        List<WebElement> socialElements = container.findElements(By.tagName("a"));
        String[] socials = super.getSocials(socialElements, false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Portugal",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "351223190888" : socials[1]
        );
    }
}
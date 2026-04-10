package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PeixotoCury extends ByNewPage {

    public PeixotoCury() {
        super(
                "Peixoto & Cury",
                "https://www.peixotoecury.com.br/profissionais",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.grid-item"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("badge")}, true);
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("card-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("badge")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("order-md-1"));
        String[] socials = super.getSocials(container.findElements(By.tagName("p")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551132188400" : socials[1]
        );
    }
}

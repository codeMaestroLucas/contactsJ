package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class AzeredoPerdigao extends ByPage {

    public AzeredoPerdigao() {
        super(
                "Azeredo Perdigão & Associados",
                "https://www.azeredoperdigao.pt/en/our-team/lawyers/Miguel-de-Azeredo-Perdigao/25/index.html",
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
        // Logic based on the provided snippet which shows a detail page as a reference for single lawyer or list entry
        return MyDriver.wait.findElements(By.id("page-content"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", this.driver.getCurrentUrl(),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("#detail-header div:first-of-type")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Portugal",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.tagName("ul")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", "351213511370"
        );
    }
}

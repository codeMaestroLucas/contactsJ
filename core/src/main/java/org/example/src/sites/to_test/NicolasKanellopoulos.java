package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class NicolasKanellopoulos extends ByNewPage {

    public NicolasKanellopoulos() {
        super(
                "Nicolas Kanellopoulos Law",
                "https://www.kanell.gr/eteri-synergates/?lang=en",
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
            return MyDriver.wait.findElements(By.className("gdlr-core-personnel-list"));
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("gdlr-core-personnel-list-title")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("gdlr-core-pbf-wrapper-container"));
        String role = extractor.extractLawyerText(container, new By[]{By.className("gdlr-core-title-item-caption")}, "ROLE", LawyerExceptions::roleException);

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        if (socials[0].isEmpty()) {
            socials = super.getSocialsFromText(container.getText());
        }

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Greece",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "2103611225" : socials[1]
        );
    }
}

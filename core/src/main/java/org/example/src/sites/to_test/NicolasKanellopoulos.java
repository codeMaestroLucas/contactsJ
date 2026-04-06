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
            int[] indexes = {3, 5, 7, 8 ,9};

            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"infinite-page-wrapper\"]/div/div/div/div/div[2]"));
            List<WebElement> lawyers = div.findElements(By.className("gdlr-core-personnel-list-column"));

            for (int index : indexes) {
                String xpath = "//*[@id=\"infinite-page-wrapper\"]/div/div/div/div/div[" + index + "]";
                div = div.findElement(By.xpath(xpath));
                lawyers.addAll(div.findElements(By.className("gdlr-core-personnel-list-column")));
            }

            return lawyers;
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

        driver.findElement(By.className("gdlr-core-pbf-wrapper-container"));
        String[] socials = super.getSocialsFromText(driver.findElement(By.xpath("//*[@id=\"infinite-page-wrapper\"]/div/div[2]/div/div/div[2]/div/div")).getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", "-----",
                "firm", this.name,
                "country", "Greece",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "2103611225" : socials[1]
        );
    }
}

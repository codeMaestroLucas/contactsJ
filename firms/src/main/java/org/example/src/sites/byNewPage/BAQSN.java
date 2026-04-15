package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BAQSN extends ByNewPage {

    public BAQSN() {
        super(
                "BAQSN",
                "https://baqsn.bo/en/partners/",
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
            return MyDriver.wait.findElements(By.className("eael-filterable-gallery-item-wrap"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".gallery-item-buttons a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("fg-item-title")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("//div/div/section/div/div[1]/div/div[2]/div"));
        String[] socials = super.getSocialsFromText(container.getText());


        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Bolivia",
                "practice_area", "",
                "email", socials[0],
                "phone", "59122799090 "
        );
    }
}

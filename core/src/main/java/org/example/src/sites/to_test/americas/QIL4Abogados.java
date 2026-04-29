package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class QIL4Abogados extends ByNewPage {

    public QIL4Abogados() {
        super(
                "QIL+4 Abogados",
                "https://www.qil4abogados.com/team",
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
        return MyDriver.wait.findElements(By.className("sqs-gallery-design-grid-slide"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.id("yui_3_17_2_1_1777417500926_73"));

        String name = extractor.extractLawyerText(container, new By[]{By.className("image-title")}, "NAME", LawyerExceptions::nameException);
        String rawSocials = extractor.extractLawyerText(container, new By[]{By.className("image-subtitle")}, "SOCIALS", LawyerExceptions::socialsException);
        String[] socials = this.getSocialsFromText(rawSocials);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Lawyer",
                "firm", this.name,
                "country", "Guatemala",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "50222054100" : socials[1]
        );
    }
}

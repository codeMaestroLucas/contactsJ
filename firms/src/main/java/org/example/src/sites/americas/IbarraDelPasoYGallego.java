package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class IbarraDelPasoYGallego extends ByNewPage {

    public IbarraDelPasoYGallego() {
        super(
                "Ibarra, del Paso y Gallego",
                "https://www.ibarrapg.com/en/lawyers/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("e-loop-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("h3 a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h4")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String profileLink = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div[1]/div[2]/div/div/div[2]/div[1]"));
        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", profileLink,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}

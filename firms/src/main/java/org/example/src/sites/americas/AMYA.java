package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class AMYA extends ByNewPage {

    public AMYA() {
        super(
                "AMYA",
                "https://www.amya.com.co/en/our-team",
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
        return MyDriver.wait.findElements(By.cssSelector("div.abogado"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h2")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("profile-wrapper"));

        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Colombia",
                "practice_area", "",
                "email", socials[0],
                "phone", "576016581100"
        );
    }
}

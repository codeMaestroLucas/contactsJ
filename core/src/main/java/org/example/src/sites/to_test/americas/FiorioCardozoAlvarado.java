package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class FiorioCardozoAlvarado extends ByNewPage {

    public FiorioCardozoAlvarado() {
        super(
                "Fiorio, Cardozo & Alvarado",
                "https://fca.com.py/profesionales-2/",
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
        return MyDriver.wait.findElements(By.className("e-loop-item"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("elementor-element-168c904"));

        String rawSocials = container.getAttribute("innerText");
        String[] socials = this.getSocialsFromText(rawSocials);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Professional",
                "firm", this.name,
                "country", "Paraguay",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "59521205052" : socials[1]
        );
    }
}

package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class FaturMenard extends ByPage {

    public FaturMenard() {
        super(
                "Fatur Menard",
                "https://fatur-menard.com/en/legal-experts/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.elementor-top-column"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        WebElement infoContainer = lawyer.findElement(By.className("elementor-cta__description"));
        String[] socials = this.getSocialsFromText(infoContainer.getAttribute("innerText"));

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException).split("\n")[0],
                "role", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h2 span")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Slovenia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "386082007348" : socials[1]
        );
    }
}

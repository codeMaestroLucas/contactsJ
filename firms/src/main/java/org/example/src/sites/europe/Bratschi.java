package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Bratschi extends ByNewPage {

    public Bratschi() {
        super(
                "Bratschi",
                "https://www.bratschi.ch/en/team?search=&personFunktion%5B11%5D=51663&personFunktion%5B4%5D=51677&personFunktion%5B5%5D=54170&personFunktion%5B6%5D=127024&personFunktion%5B7%5D=202451&personFunktion%5B10%5D=51662&personFunktion%5B12%5D=51568&personFunktion%5B13%5D=51612&personFunktion%5B14%5D=51543&offset=0&limit=12&viewmode=card",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

        // More than 30 clicks
        MyDriver.clickOnElementMultipleTimes(
                By.id("load-more-oob"),
                3, 0.5
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("article.border-bottom"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div > small.text-grey:last-child")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("col-lg-7"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Switzerland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "41582581600" : socials[1]
        );
    }
}

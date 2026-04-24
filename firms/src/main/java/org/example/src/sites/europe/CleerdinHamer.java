package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class CleerdinHamer extends ByNewPage {

    public CleerdinHamer() {
        super(
                "CLEERDIN & HAMER",
                "https://cleerdin-hamer.nl/advocaten/",
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
        WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"fusion-portfolio-1\"]"));
        List<WebElement> lawyers = div.findElements(By.className("fusion-portfolio-post"));

        div = driver.findElement(By.xpath("//*[@id=\"fusion-portfolio-2\"]"));
        lawyers.addAll(div.findElements(By.className("fusion-portfolio-post")));

        div = driver.findElement(By.xpath("//*[@id=\"fusion-portfolio-3\"]"));
        lawyers.addAll(div.findElements(By.className("fusion-portfolio-post")));

        return lawyers;
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("fusion-link-wrapper")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.tagName("body"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("h1.fusion-title-heading")}, "NAME", "textContent", LawyerExceptions::nameException),
                "role", "Partner",
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31206750756" : socials[1]
        );
    }
}

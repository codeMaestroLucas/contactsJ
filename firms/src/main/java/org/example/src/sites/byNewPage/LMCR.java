package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class LMCR extends ByNewPage {

    public LMCR() {
        super(
                "LMCR",
                "https://www.lmcr.it/lmcr/en/professionisti/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {

        WebElement div = MyDriver.wait.findElement(By.xpath("/html/body/div[3]/div"));
        List<WebElement> lawyers = div.findElements(By.cssSelector("div.col-md-4 div.text-center"));

        div = driver.findElement(By.xpath("/html/body/div[4]/div/div"));
        lawyers.addAll(div.findElements(By.cssSelector("div.col-md-4 div.text-center")));

        return lawyers;
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div[3]/div/div[2]"));
        String[] socials = super.getSocialsFromText(extractor.extractLawyerText(container, new By[]{By.className("text-baskerville")}, "SOCIALS", LawyerExceptions::socialsException));

        return Map.of(
                "link", link,
                "name", name,
                "role", "----",
                "firm", this.name,
                "country", "Italy",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "39023030351" : socials[1]
        );
    }
}

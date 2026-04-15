package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class ZuzunagaAssereto extends ByNewPage {

    public ZuzunagaAssereto() {
        super(
                "Zuzunaga & Assereto",
                "https://zyaabogados.com/en/abogados/",
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
            WebElement div = MyDriver.wait.findElement(By.id("socios"));
            List<WebElement> lawyers = div.findElements(By.className("rt-grid-item"));

            div = driver.findElement(By.id("consejero"));
            lawyers.addAll(div.findElements(By.className("rt-grid-item")));

            div = driver.findElement(By.id("asociados-senior"));
            lawyers.addAll(div.findElements(By.className("rt-grid-item")));

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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("team-name")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("tlp-member-detail"));
        String[] socials = super.getSocialsFromText(extractor.extractLawyerText(container, new By[]{By.tagName("p")}, "SOCIALS", LawyerExceptions::socialsException));

        return Map.of(
                "link", link,
                "name", name,
                "role", "----",
                "firm", this.name,
                "country", "Peru",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "5117708490" : socials[1]
        );
    }
}

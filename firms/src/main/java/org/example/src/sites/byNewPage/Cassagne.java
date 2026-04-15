package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Cassagne extends ByNewPage {

    public Cassagne() {
        super(
                "CASSAGNE",
                "https://www.cassagne.com.ar/equipo/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    String[] validRoles = {"socio", "socia"};

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebElement div = MyDriver.wait.findElement(By.xpath("/html/body/div/section[2]/div[2]/div/div/div/div/div"));
            List<WebElement> lawyers = div.findElements(By.cssSelector("div[data-elementor-type=\"loop-item\"]"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.tagName("div")}, false);
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
        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div/section/div/div[1]/div/section[2]/div"));
        String name = extractor.extractLawyerAttribute(MyDriver.getINSTANCE().findElement(By.tagName("body")), new By[]{By.tagName("h1")}, "NAME", "textContent", LawyerExceptions::nameException);
        List<WebElement> socialLinks = container.findElements(By.tagName("a"));
        String[] socials = super.getSocials(socialLinks, false);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Argentina",
                "practice_area", "",
                "email", socials[0],
                "phone", "541141297200"
        );
    }
}

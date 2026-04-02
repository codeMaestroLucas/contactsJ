package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class UMBRA extends ByNewPage {

    public UMBRA() {
        super(
                "UMBRA",
                "https://umbra.law/lawyers/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"wpaas-accordion-1\"]/div[2]/div"));
        List<WebElement> lawyers = div.findElements(By.className("as-wposslide"));

        div = driver.findElement(By.xpath("//*[@id=\"wpaas-accordion-2\"]/div[2]/div"));
        lawyers.addAll(div.findElements(By.className("as-wposslide")));

        div = driver.findElement(By.xpath("//*[@id=\"wpaas-accordion-3\"]/div[2]/div"));
        lawyers.addAll(div.findElements(By.className("as-wposslide")));

        div = driver.findElement(By.xpath("//*[@id=\"wpaas-accordion-4\"]/div[2]/div"));
        lawyers.addAll(div.findElements(By.className("as-wposslide")));

        div = driver.findElement(By.xpath("//*[@id=\"wpaas-accordion-5\"]/div[2]/div"));
        lawyers.addAll(div.findElements(By.className("as-wposslide")));

        div = driver.findElement(By.xpath("//*[@id=\"wpaas-accordion-6\"]/div[2]/div"));
        lawyers.addAll(div.findElements(By.className("as-wposslide")));

        return lawyers;
    }

    private String getRole(WebElement content) throws LawyerExceptions {
        String role = extractor.extractLawyerText(content, new By[]{By.tagName("em")}, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
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

        String socialsText = MyDriver.wait.findElement(By.xpath("//div/div/div[1]/div[2]/div/div[2]/div/div/div[1]/div")).getText();
        String[] socials = super.getSocialsFromText(socialsText);

        return Map.of(
                "link", link,
                "name", MyDriver.wait.findElement(By.xpath("//div/div/div[1]/div[2]/div/div[2]/div/div/div[1]/div/p[1]/span/strong/span")).getText(),
                "role", MyDriver.wait.findElement(By.xpath("//div/div/div[1]/div[2]/div/div[2]/div/div/div[1]/div/p[2]/em/strong")).getText(),
                "firm", this.name,
                "country", "Indonesia",
                "practice_area", MyDriver.wait.findElement(By.xpath("//div/div/div[1]/div[2]/div/div[2]/div/div/div[3]/div/p[2]")).getText(),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "622150820999" : socials[1]
        );
    }
}

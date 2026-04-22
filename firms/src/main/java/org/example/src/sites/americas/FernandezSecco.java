package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class FernandezSecco extends ByNewPage {

    public FernandezSecco() {
        super(
                "Fernández Secco",
                "https://fernandezsecco.com/en/equipo/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("miembro-no-vacio"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String profileLink = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.xpath("//*[@id=\"inner-content\"]/article/div/section/div/div/div/section/div/div[1]/div"));

        String name = extractor.extractLawyerAttribute(container, new By[]{By.tagName("h2")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(container, new By[]{By.tagName("h4")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String[] socials = this.getSocials(container.findElements(By.cssSelector(".elementor-social-icon")), false);

        return Map.of(
                "link", profileLink,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Uruguay",
                "practice_area", "",
                "email", socials[0],
                "phone", "59829161913"
        );
    }
}
package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BullardFallaEzcurra extends ByNewPage {

    public BullardFallaEzcurra() {
        super(
                "Bullard, Falla, Ezcurra",
                "https://bullardfallaezcurra.com/en/our-team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("tm-container"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector(".team-pic a")).getAttribute("href");
        link = link.replace("member-profile", "member-cv");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = lawyer.findElement(By.className("has-custom-font")).getText();
        String role = lawyer.findElement(By.cssSelector("h5 strong")).getText();
        String area = lawyer.findElement(By.xpath(".//h5[last()]")).getText();

        String profileLink = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("team-info"));
        String[] socials = super.getSocialsFromText(container.getText());


        return Map.of(
                "link", profileLink,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Peru",
                "practice_area", area,
                "email", socials[0],
                "phone", "5116211515"
        );
    }
}

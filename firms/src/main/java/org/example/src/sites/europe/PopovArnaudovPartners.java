package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PopovArnaudovPartners extends ByNewPage {

    public PopovArnaudovPartners() {
        super(
                "Popov, Arnaudov & Partners",
                "https://popovarnaudov.bg/en/our-team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("vc_grid-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("employee-position-grid-title")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("vc_gitem-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("employee-intro"));

        String[] socials = this.getSocials(container.findElements(By.tagName("p")), true);
        if (socials[0].isEmpty()) socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", extractor.extractLawyerText(container, new By[]{By.className("position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Bulgaria",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35928581901" : socials[1]
        );
    }
}

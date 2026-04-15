package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Prieto extends ByNewPage {

    public Prieto() {
        super(
                "Prieto",
                "https://www.prieto.cl/en/equipo-4/",
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
            return MyDriver.wait.findElements(By.cssSelector("div.filtr-item"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.xpath("./parent::a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {

        String link = this.openNewTab(lawyer);
        WebElement contact = driver.findElement(By.xpath("//div[contains(@class, 'contactos')]"));
        
        String role = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("//div[2]/div[1]/div/div[2]/div[3]/div/p")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String[] socials = super.getSocials(contact.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", role,
                "role", "Partner",
                "firm", this.name,
                "country", "Chile",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "56222805000" : socials[1]
        );
    }
}

package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class BruchouFunesDeRioja extends ByNewPage {

    public BruchouFunesDeRioja() {
        super(
                "Bruchou & Funes de Rioja",
                "https://bruchoufunes.com/en/profesionales/",
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
        return MyDriver.wait.findElements(By.cssSelector("a.capsula-our-people"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("capsula-our-people--tag")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("/html/body/main/section/div/div/div[2]/div[1]/div/div[2]"));

        String role = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.className("fc-orange")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String pa = driver.findElement(By.className("our-people-details--practice-areas")).getAttribute("textContent");
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Argentina",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1]
        );
    }
}

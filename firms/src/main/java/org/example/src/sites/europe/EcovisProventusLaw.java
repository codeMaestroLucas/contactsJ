package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class EcovisProventusLaw extends ByNewPage {

    public EcovisProventusLaw() {
        super(
                "Ecovis ProventusLaw",
                "https://ecovis.lt/team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.team-member-card"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("member-position")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("member-position")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("member-details"));
        String[] socials = super.getSocialsFromText(container.getText());
        String pa = extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[]{By.xpath("//div/header/div/div[2]/div/div/div[2]/div")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Lithuania",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "37052124084" : socials[1]
        );
    }
}

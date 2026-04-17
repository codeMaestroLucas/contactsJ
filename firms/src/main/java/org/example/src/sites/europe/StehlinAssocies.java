package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class StehlinAssocies extends ByNewPage {

    public StehlinAssocies() {
        super(
                "Stehlin & Associés",
                "https://everlaw-tax.fr/en/lawyers/",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("dima-team"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("member-function")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("icons-media"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("member-name")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("member-function")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("dimacell"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        socials[0] = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div/div/div/section/div/div/div/div/div[2]/div/div/div/ul/li[1]")).getAttribute("textContent");
        String practice = extractor.extractLawyerAttribute(container, new By[]{By.xpath("/html/body/div[1]/div[1]/div/div/div/section/div/div/div/div/div[2]/div/div/div/div/div/div/div/div[1]/div/div/span/strong"), By.tagName("span")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "France",
                "practice_area", practice,
                "email", socials[0],
                "phone", "33158183333"
        );
    }
}

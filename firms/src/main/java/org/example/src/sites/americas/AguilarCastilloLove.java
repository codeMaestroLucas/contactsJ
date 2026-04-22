package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class AguilarCastilloLove extends ByNewPage {

    public AguilarCastilloLove() {
        super(
                "Aguilar Castillo Love",
                "https://www.aguilarcastillolove.com/our-people",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.image-wrapper"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".meta-title span")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".meta-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".meta-title span")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        String[] socials = null;
        WebElement container = MyDriver.wait.findElement(By.id("page"));
        try {
            Thread.sleep(500);
            socials = super.getSocialsFromText(container.getText());
        } catch (Exception e) {
            socials = super.getSocials(container.findElements(By.tagName("p")), true);
        }

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Guatemala",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "50222170300" : socials[1]
        );
    }
}

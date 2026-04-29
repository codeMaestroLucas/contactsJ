package org.example.src.sites.to_test._standingBy;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PepeljugoskiLaw extends ByNewPage {

    public PepeljugoskiLaw() {
        super(
                "Pepeljugoski Law",
                "https://pepeljugoski.com.mk/en/our-team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.elementor-column-wrap.elementor-element-populated"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("h4 > a[href*='https://pepeljugoski.com.mk/en/']")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("span")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.tagName("body"));
        String[] socials = super.getSocialsFromText(container.getText());
        if (socials[0].isEmpty()) socials = super.getSocialsFromText(container.getAttribute("innerText"));

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "North Macedonia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "38923211197" : socials[1]
        );
    }
}

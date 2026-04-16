package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class ABLPartnersLP extends ByNewPage {

    public ABLPartnersLP() {
        super(
                "ABL Partners LP",
                "https://ablpartnerslp.com/about/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.expert-single"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("span")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("attorneys-info"));

        String role = extractor.extractLawyerText(container, new By[]{By.xpath("//li[contains(.,'Positon:')]")}, "ROLE", LawyerExceptions::roleException).replace("Positon:", "").trim();
        String[] socials = super.getSocials(container.findElements(By.tagName("li")), true);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath("//li[contains(.,'Practice Area:')]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException).replace("Practice Area:", "").trim(),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "2348182824007" : socials[1]
        );
    }
}

package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PRKPartners extends ByNewPage {

    public PRKPartners() {
        super(
                "PRK Partners",
                "https://www.prkpartners.com/our-team?search_advanced%5Bpersons_name%5D=a&search_advanced%5Bpersons_specs%5D=&search_advanced%5Bpersons_places%5D=&submit=submit#search_advanced",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.entry-persons"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("h4")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "ROLE", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerText(lawyer, new By[]{By.className("entry-meta-value")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.xpath("//*[@id=\"main\"]/div[1]/div[1]/div/div[1]/div"));

        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Czech Republic",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "420221430111" : socials[1]
        );
    }
}

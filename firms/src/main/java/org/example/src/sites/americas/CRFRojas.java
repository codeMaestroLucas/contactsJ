package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class CRFRojas extends ByNewPage {

    public CRFRojas() {
        super(
                "CRF Rojas",
                "https://rojas-lawfirm.com/abogados/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.rollDownToBottom(0.2);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.abogado"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("elementor-post-info__terms-list")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-heading-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-post-info__terms-list")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//*[@id=\"main\"]/div/section[1]/div[2]/div[1]/div"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Bolivia",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("elementor-element-49a5901")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "59122113165" : socials[1]
        );
    }
}

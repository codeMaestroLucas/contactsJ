package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MeabyCoSolicitors extends ByNewPage {

    public MeabyCoSolicitors() {
        super(
                "Meaby&Co Solicitors",
                "https://www.meaby.co.uk/meet-the-team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".flex_column.av-zero-column-padding"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("p")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h5")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("av-cfifet-58f00ad60f1b1a9314876cf4111d0533"));

        String[] socials = this.getSocials(container.findElements(By.cssSelector(".iconbox_content_title a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "United Kingdom",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath(".//h5[contains(text(),'Practice Areas')]/following-sibling::p")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[1],
                "phone", socials[0].isEmpty() ? "442077035034" : socials[0]
        );
    }
}

package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BrouxelRabia extends ByNewPage {

    public BrouxelRabia() {
        super(
                "Brouxel & Rabia",
                "https://brouxelrabia.lu/our-lawyers/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.ue-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("ue-item-text")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("uc_more_btn")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("ue-item-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("ue-item-text")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.className("elementor-element-72ef5ea"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        String practices = container.findElements(By.cssSelector(".expertise-col .dce-post-title a")).stream()
                .map(WebElement::getText)
                .collect(Collectors.joining(", "));

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Luxembourg",
                "practice_area", practices,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "352262626" : socials[1]
        );
    }
}

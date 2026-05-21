package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class OrangeClover extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public OrangeClover() {
        super(
                "Orange Clover",
                "https://orangecloverlaw.com/person/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("elementor-grid-item"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("elementor-heading-title")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-page-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-widget-heading")}, "ROLE", LawyerExceptions::roleException);
        
        String vcardHref = lawyer.findElement(By.xpath("//a[contains(@href, '.vcf')]")).getAttribute("href");
        String[] socials = vCard.getSocials(vcardHref);

        String link = this.openNewTab(lawyer);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Netherlands",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31202400891" : socials[1]
        );
    }
}

package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Cresco extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public Cresco() {
        super(
                "Cresco",
                "https://crescolaw.com/team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("jet-listing-grid__item"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("jet-listing-dynamic-field__content")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("jet-engine-listing-overlay-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("jet-listing-dynamic-field__content")}, "NAME", LawyerExceptions::nameException);
        
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("elementor-widget-populated"));

        String vcardHref = MyDriver.wait.findElement(By.xpath("//a[contains(@href, '.vcf')]")).getAttribute("href");
        String[] socials = vCard.getSocials(vcardHref);

        String role = extractor.extractLawyerText(driver.findElement(By.className("jet-tabs__content")), new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Belgium",
                "practice_area", "",
                "email", socials[0].isEmpty() ? extractor.extractLawyerAttribute(container, new By[]{By.xpath("//a[contains(@href, 'mailto:')]")}, "EMAIL", "href", LawyerExceptions::emailException).replace("mailto:", "") : socials[0],
                "phone", socials[1].isEmpty() ? "3234123456" : socials[1]
        );
    }
}

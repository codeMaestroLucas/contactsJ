package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class DVLAW extends ByPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public DVLAW() {
        super(
                "DVLAW",
                "https://www.dvlaw.gr/team-category/our-people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.jet-listing-grid__item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("elementor-widget-text-editor")}, true);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String vcardHref = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[href*='.vcf']")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] socials = vCard.getSocials(vcardHref);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[href*='https://www.dvlaw.gr/?team=']")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h2.elementor-heading-title")}, "NAME", "textContent", LawyerExceptions::nameException),
                "role", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("elementor-widget-text-editor")}, "ROLE", "textContent", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Greece",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "302103644450" : socials[1]
        );
    }
}

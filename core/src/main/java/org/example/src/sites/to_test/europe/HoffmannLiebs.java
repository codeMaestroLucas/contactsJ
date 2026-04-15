package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class HoffmannLiebs extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public HoffmannLiebs() {
        super(
                "Hoffmann Liebs",
                "https://www.hoffmannliebs.de/en/?searchType=PROPERTIES&s=",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("ul.wse-results > li"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("wse-result-position")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("wse-result-name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("wse-result-position")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("elementor-widget-wrap"));
        String vcardHref = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href*='.vcf']")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] socialsVCard = vCard.getSocials(vcardHref);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Germany",
                "practice_area", "",
                "email", socials[0].isEmpty() ? socialsVCard[0] : socials[0],
                "phone", socials[1].isEmpty() ? socialsVCard[1] : socials[1]
        );
    }
}

package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MirandaAssociados extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public MirandaAssociados() {
        super(
                "Miranda & Associados",
                "https://www.mirandalawfirm.com/en/our-people",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("grid__box"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("grid__box__subtext")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("grid__box__text")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("grid__box__subtext")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("banner-slider__bg__content"));

        String vcardHref = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href*='vcard']")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] socials = vCard.getSocials(this.driver, vcardHref);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Portugal",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("small")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "351217814800" : socials[1]
        );
    }
}

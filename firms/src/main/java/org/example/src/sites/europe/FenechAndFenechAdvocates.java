package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class FenechAndFenechAdvocates extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public FenechAndFenechAdvocates() {
        super(
                "Fenech & Fenech Advocates",
                "https://fenechlaw.com/people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.partner"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("h6.designation")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h4.name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h6.designation")}, "ROLE", LawyerExceptions::roleException);
        String practiceArea = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("p.practice-areas")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("people-banner"));

        String vcardHref = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href*='vcard']")}, "VCARD", "href", (e) -> null);
        String[] socials = (vcardHref != null) ? vCard.getSocials(vcardHref) : new String[]{"", ""};

        if (socials[0].isEmpty()) {
            socials[0] = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href^='mailto:']")}, "EMAIL", "href", LawyerExceptions::emailException).replace("mailto:", "");
        }

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Malta",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35621241232" : socials[1]
        );
    }
}

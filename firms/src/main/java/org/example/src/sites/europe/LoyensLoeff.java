package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class LoyensLoeff extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public LoyensLoeff() {
        super(
                "Loyens & Loeff",
                "https://www.loyensloeff.com/people/?Titles=Partner",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElement(By.xpath("/html/body/main/section/div/div/div/div/div[2]/div/div[2]/button"));

        // More than 30 rolls
        MyDriver.rollDown(5, 0.6);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("a.people-card"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("function")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("function")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("info"));

        String pa = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.cssSelector("a[href*='https://www.loyensloeff.com/services/expertises/']")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        String vcardHref = MyDriver.wait.findElement(By.cssSelector("a[href*='GetVCard']")).getAttribute("href");
        String[] socials = vCard.getSocials(this.driver, vcardHref);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31102246288" : socials[1]
        );
    }
}

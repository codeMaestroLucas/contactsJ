package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class AugustDebouzy extends ByPage {
    private final VCard vCard = VCard.withDefaultPatterns();

    public AugustDebouzy() {
        super(
                "August Debouzy",
                "https://www.august-debouzy.com/en/team",
                8
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        } else {
            MyDriver.clickOnAddBtn(By.xpath("//*[@id=\"vue-lawyers\"]/div/button[10]"));
            Thread.sleep(1500);
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".tw-grid-cols-team"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("job")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div:first-child")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("job")}, "ROLE", LawyerExceptions::roleException);
        String vcardUrl = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[href*='/vcard/']")}, "VCARD", "href", (e) -> null);

        String[] socials = {"", ""};
        if (vcardUrl != null) {
            socials = vCard.getSocials(this.driver, vcardUrl);
        }

        return Map.of(
                "link", this.link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "France",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "33144355500" : socials[1]
        );
    }
}
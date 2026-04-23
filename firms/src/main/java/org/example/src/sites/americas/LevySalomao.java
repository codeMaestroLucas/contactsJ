package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class LevySalomao extends ByPage {

    public LevySalomao() {
        super(
                "Levy & Salomão",
                "https://www.levysalomao.com.br/professionals",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.member"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("span")}, true);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String vcardUrl = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[title='vCard']")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] vcardData = VCard.withDefaultPatterns().getSocials(vcardUrl);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".link a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.tagName("span")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", vcardData[0],
                "phone", vcardData[1].isEmpty() ? "551135555000" : vcardData[1]
        );
    }
}
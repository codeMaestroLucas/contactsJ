package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class KaratzasAndPartners extends ByNewPage {

    public KaratzasAndPartners() {
        super(
                "Karatzas & Partners",
                "https://karatza-partners.gr/ourpeople/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div[acc-name='OurPeople_Card']"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("span[acc-name='text_1']")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[acc-name='htmlelement']")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("span[acc-name='text']")}, "FNAME", LawyerExceptions::nameException) + " " +
                extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("span[acc-name='text_2']")}, "LNAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("span[acc-name='text_1']")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.cssSelector("div[acc-name='tags_list']"));

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Greece",
                "practice_area", extractor.extractLawyerText(container, new By[]{}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", "",
                "phone", "302103386100"
        );
    }
}

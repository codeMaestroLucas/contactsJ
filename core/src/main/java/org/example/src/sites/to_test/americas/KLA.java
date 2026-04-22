package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class KLA extends ByNewPage {

    public KLA() {
        super(
                "KLA",
                "https://klalaw.com.br/en/team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        } else {
            MyDriver.clickOnElement(By.cssSelector("div.jet-filters-pagination__item.prev-next.next"));
            Thread.sleep(2000);
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("div.jet-listing-grid__item"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("jet-engine-listing-overlay-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("jet-listing-dynamic-field__content")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div/div/div/div[2]/div/div[1]/div[2]"));
        String[] socials = super.getSocials(container.findElements(By.tagName("li")), true);

        return Map.of(
                "link", link,
                "name", name,
                "role", "",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551137998100" : socials[1]
        );
    }
}

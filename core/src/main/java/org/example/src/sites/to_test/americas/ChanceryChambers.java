package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class ChanceryChambers extends ByNewPage {

    public ChanceryChambers() {
        super(
                "Chancery Chambers",
                "https://chancerychambers.com/professionals/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".uabb-team-member-wrap"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("uabb-team-desc")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        WebElement bioButton = lawyer.findElement(By.xpath("./following-sibling::div//a[contains(@class, 'uabb-trigger')]"));
        bioButton.click();
        return this.link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("uabb-team-name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("uabb-team-desc")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("fl-col-content"));

        String[] socials = this.getSocials(container.findElements(By.cssSelector(".fl-icon-group a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Barbados",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "12464343400" : socials[1]
        );
    }
}

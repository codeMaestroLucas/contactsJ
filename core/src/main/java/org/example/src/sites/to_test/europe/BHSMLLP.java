package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BHSMLLP extends ByNewPage {

    public BHSMLLP() {
        super(
                "BHSM LLP",
                "https://bhsm.ie/people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("people__list-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("people__list-item__role")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("people__list-item__name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("people__list-item__role")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer.findElement(By.tagName("a")));
        WebElement container = MyDriver.wait.findElement(By.className("person__contact"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Ireland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35314408300" : socials[1]
        );
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }
}

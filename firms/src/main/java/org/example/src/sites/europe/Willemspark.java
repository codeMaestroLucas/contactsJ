package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Willemspark extends ByNewPage {

    public Willemspark() {
        super(
                "WILLEMSPARK",
                "https://willemsparkadvocaten.nl/advocaten/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".card-team"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("card-team__side__function")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("card-team__side__button")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("card-team__side__name")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = null;
        try {
            container = MyDriver.wait.findElement(By.xpath("/html/body/div[2]/section[3]/div"));
        } catch (Exception e) {
            container = driver.findElement(By.tagName("body"));
        }

        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);
        String phone = socials[1].isEmpty() ? this.getSocialsFromText(container.getText())[1] : socials[1];

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", "",
                "email", socials[0],
                "phone", phone.isEmpty() ? "31203640310" : phone
        );
    }
}

package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class NewBalkansLaw extends ByNewPage {

    public NewBalkansLaw() {
        super(
                "New Balkans Law",
                "https://www.newbalkanslawoffice.com/our-people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("profile-card"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("profile-card__position")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("profile-card__name"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("profile-card__name")}, "NAME", LawyerExceptions::nameException);
        
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("hero__author-meta"));

        String role = extractor.extractLawyerText(MyDriver.wait.findElement(By.className("hero__title")), new By[]{By.tagName("h4")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Bulgaria",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35929963868" : socials[1]
        );
    }
}

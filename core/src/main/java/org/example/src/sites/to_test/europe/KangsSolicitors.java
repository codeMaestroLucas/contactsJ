package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class KangsSolicitors extends ByNewPage {

    public KangsSolicitors() {
        super(
                "Kangs Solicitors",
                "https://www.kangssolicitors.co.uk/people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.card--person"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("card-position")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("card-name")}, "NAME", LawyerExceptions::nameException);
        
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("post-col-l--profile"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("profile-position")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = this.getSocials(container.findElements(By.cssSelector(".contact-info a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "United Kingdom",
                "practice_area", extractor.extractLawyerText(MyDriver.wait.findElement(By.className("post-row")), new By[]{By.className("expertise-list")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "441214499888" : socials[1]
        );
    }
}

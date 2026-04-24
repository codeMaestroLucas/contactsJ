package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class RassersAdvocaten extends ByNewPage {

    public RassersAdvocaten() {
        super(
                "Rassers Advocaten",
                "https://rassers.nl/team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("gallery-block"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("h4")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("image")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("ty_member_cta"));

        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[]{By.xpath("/html/body/section[1]/div/div/div/div/h2[1]")}, "ROLE", "textContent", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", extractor.extractLawyerAttribute(container, new By[]{By.className("ty-member_pages")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31765136192" : socials[1]
        );
    }
}

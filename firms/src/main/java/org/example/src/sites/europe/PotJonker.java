package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PotJonker extends ByNewPage {

    public PotJonker() {
        super(
                "Pot Jonker",
                "https://www.potjonker.nl/en/our-people/",
                4
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.potjonker.nl/en/our-people/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        WebElement div = MyDriver.wait.findElement(By.xpath("/html/body/main/div/section[2]/div"));
        List<WebElement> lawyers = div.findElements(By.className("card--employee"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("h-text-bold")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("card__title")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("h-text-bold")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("span.h-color-black:not(.h-text-bold)")}, "PRACTICE", "textContent", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("card__content"));

        String[] socials = super.getSocials(container.findElements(By.className("card__link")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "310235530246" : socials[1]
        );
    }
}

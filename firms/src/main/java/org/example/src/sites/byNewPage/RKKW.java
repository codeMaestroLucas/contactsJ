package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class RKKW extends ByNewPage {

    public RKKW() {
        super(
                "RKKW",
                "https://rkkw.pl/en/team/",
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
        try {
            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"page\"]/div[1]/div[1]/div/div"));
            List<WebElement> lawyers = div.findElements(By.className("pracownik"));

            div = driver.findElement(By.xpath("//*[@id=\"page\"]/div[1]/div[2]/div/div"));
            lawyers.addAll(div.findElements(By.className("pracownik")));

            div = driver.findElement(By.xpath("//*[@id=\"page\"]/div[1]/div[3]/div/div"));
            lawyers.addAll(div.findElements(By.className("pracownik")));

            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("nazwisko")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("stanowisko")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.cssSelector("div.inner span.wraper"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Poland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "48225417080" : socials[1]
        );
    }
}

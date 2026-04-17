package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MitraniCaballero extends ByNewPage {

    public MitraniCaballero() {
        super(
                "Mitrani Caballero & Ruiz Moreno",
                "https://mitranicaballero.com/team-category/?lang=en#partners",
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

            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"main\"]/div/div[3]"));
            List<WebElement> lawyers = div.findElements(By.cssSelector("a[href*='https://mitranicaballero.com/team/']"));

            div = driver.findElement(By.xpath("//*[@id=\"main\"]/div/div[5]"));
            lawyers.addAll(div.findElements(By.cssSelector("a[href*='https://mitranicaballero.com/team/']")));

            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("//div/div[2]/div/div[2]/div"));

        String role = extractor.extractLawyerText(MyDriver.wait.findElement(By.tagName("body")), new By[]{By.xpath("//div/div[1]/div/div/div/div[2]/div[1]/div/div[2]/h2")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Argentina",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.cssSelector("a[href*='/practices/']")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "541145904700" : socials[1]
        );
    }
}

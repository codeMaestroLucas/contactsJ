package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Aninat extends ByNewPage {

    public Aninat() {
        super("Aninat", "https://aninat.cl/en/team/", 1);
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        int[] indexes = {4, 6, 8 ,9};
        WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"post-6868\"]/div/div/section[3]"));
        List<WebElement> lawyers = div.findElements(By.cssSelector("a[href*='https://aninat.cl/en/']"));

        for (int index : indexes) {
            String xpath = "//*[@id=\"post-6868\"]/div/div/section[" + index + "]";
            div = driver.findElement(By.xpath(xpath));
            lawyers.addAll(div.findElements(By.cssSelector("a[href*='https://aninat.cl/en/']")));
        }

        return lawyers;
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);

        WebElement body = driver.findElement(By.tagName("body"));
        WebElement container = null;
        try {
            container = MyDriver.wait.findElement(By.xpath("//div/div/section/div[2]/div[2]/div/div[7]/div/ul"));
        } catch (Exception e) {
            container = body;
        }
        String name = extractor.extractLawyerAttribute(body, new By[]{By.xpath("//div/div/section/div[2]/div[2]/div/div[3]/div/h3")}, "NAME", "textContent", LawyerExceptions::nameException);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", "----",
                "firm", this.name,
                "country", "Chile",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "+56224843000" : socials[1]
        );
    }
}

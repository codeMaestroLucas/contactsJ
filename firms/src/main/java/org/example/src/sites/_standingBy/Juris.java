package org.example.src.sites._standingBy;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Juris extends ByNewPage {

    public Juris() {
        super(
                "Juris",
                "https://www.juris.is/en/starfsfolk/allt-starfsfolk",
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
        WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"main\"]/div[1]/div[2]/main/div/div/div[2]"));
        List<WebElement> lawyers = div.findElements(By.cssSelector("div[class*='framer-'][class*='-container']"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String text = lawyer.getText();

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//*[@id=\"main\"]/div[1]/div[2]/main/div/div/div[2]"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", text.split("\n")[0],
                "role", text.split("\n")[1],
                "firm", this.name,
                "country", "Iceland",
                "practice_area", container.getText(),
                "email", socials[0],
                "phone", "3545203500"
        );
    }
}

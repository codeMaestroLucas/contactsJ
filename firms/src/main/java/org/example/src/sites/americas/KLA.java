package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class KLA extends ByNewPage {

    public KLA() {
        super(
                "KLA",
                "https://klalaw.com.br/en/team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        } else {
            MyDriver.clickOnElement(By.cssSelector("div.jet-filters-pagination__item.prev-next.next"));
        }
        Thread.sleep(2000);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"content\"]/div/div/div[2]/div/div/div/div[2]/div/div[1]/div/div/div"));
        return div.findElements(By.cssSelector("a"));
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

        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div[2]/div[1]/div/div/div/div[2]/div/div[1]/div[2]"));
        String name = extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[]{By.xpath("/html/body/div[2]/div[2]/div/div/div/div/div/div/div[2]/div/h2")}, "NAME", "textContent", LawyerExceptions::nameException);
        String pa = extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[]{By.xpath("/html/body/div[2]/div[2]/div/div/div/div/div/div/div[4]/div/div[1]/div/div/p")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);
        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", "",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551137998100" : socials[1]
        );
    }
}

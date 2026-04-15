package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class GuerreroOlivos extends ByNewPage {

    public GuerreroOlivos() {
        super(
                "Guerrero Olivos",
                "https://guerrero.cl/en/lawyers/",
                9
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        } else {
            MyDriver.clickOnElement(By.xpath("//*[@id=\"content\"]/div/div/div[3]/div/div[2]/div/div/div/div[10]/div"));
            Thread.sleep(3500);
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div[data-elementor-type=\"loop-item\"]"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.cssSelector("h2 > span")}, false);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h2 > span")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div[2]/div[1]/div/div[2]/div"));

        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Chile",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "56226742900" : socials[1]
        );
    }
}

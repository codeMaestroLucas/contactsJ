package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class CPB extends ByNewPage {

    public CPB() {
        super(
                "CPB",
                "https://www.cpb-abogados.com.pe/en/equipo/",
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
            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"post-4968\"]/div/div[4]/div"));
            List<WebElement> lawyers = div.findElements(By.cssSelector("div.fusion-layout-column"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("person-content")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("a[href*='https://www.cpb-abogados.com.pe/en/']")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("person-name")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("person-content")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String link = this.openNewTab(lawyer);

        String email = MyDriver.wait.findElement(By.xpath("//div/div[2]/div/div[1]/div/div[2]/div[1]/div/div/h2")).getAttribute("textContent");

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Peru",
                "practice_area", "",
                "email", email,
                "phone", "5112053030"
        );
    }
}

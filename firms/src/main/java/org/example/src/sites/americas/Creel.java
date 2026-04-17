package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Creel extends ByNewPage {

    public Creel() {
        super(
                "Creel",
                "https://www.creel.mx/en/our-lawyers/",
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
            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"page\"]/section[1]/div[2]/div/div"));
            List<WebElement> lawyers = div.findElements(By.tagName("li"));

            div = driver.findElement(By.xpath("//*[@id=\"page\"]/section[2]/div[2]/div/div/ul"));
            lawyers.addAll(div.findElements(By.tagName("li")));

            div = driver.findElement(By.xpath("//*[@id=\"page\"]/section[3]/div[2]/div/div"));
            lawyers.addAll(div.findElements(By.tagName("li")));

            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("a")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("grid__box-abogados-float"));
        String role = extractor.extractLawyerText(container, new By[]{By.id("page__p-main")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocials(container.findElements(By.className("single__perfil-link")), false);
        String practice = extractor.extractLawyerText(container, new By[]{By.className("single__perfil-category")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "525547480600" : socials[1]
        );
    }
}

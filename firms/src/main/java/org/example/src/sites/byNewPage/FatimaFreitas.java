package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class FatimaFreitas extends ByNewPage {

    public FatimaFreitas() {
        super(
                "Fátima Freitas",
                "https://www.fatimafreitas.com/en/people",
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
            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"socios\"]/div[2]"));
            List<WebElement> lawyers = div.findElements(By.cssSelector("a"));

            div = driver.findElement(By.xpath("//*[@id=\"of-counsels\"]"));
            lawyers.addAll(div.findElements(By.cssSelector("a")));

            div = driver.findElement(By.xpath("//*[@id=\"managing-associates\"]"));
            lawyers.addAll(div.findElements(By.cssSelector("a")));

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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("azul")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.id("curriculopartner"));
        String role = extractor.extractLawyerText(container, new By[]{By.className("azulescuro")}, "ROLE", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerText(container, new By[]{By.id("labelarea"), By.xpath("following-sibling::ul")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Angola",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "244222372030" : socials[1]
        );
    }
}
package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MitraniCaballero extends ByNewPage {

    public MitraniCaballero() {
        super(
                "Mitrani Caballero & Ruiz Moreno",
                "https://mitranicaballero.com/equipo/?lang=en",
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
            return MyDriver.wait.findElements(By.cssSelector("a.persona"));
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
        // Name is split in two h2 tags in the list
        String name = MyDriver.wait.findElements(By.tagName("h2")).stream()
                .map(WebElement::getText)
                .collect(Collectors.joining(" "));

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("//*[@id=\"post-1774\"]/div/div[2]/div/div[2]/div"));

        // Role is usually not in the list or the contact div provided,
        // but based on common structures for this firm we extract it from the page
        String role = extractor.extractLawyerText(MyDriver.wait.findElement(By.tagName("body")), new By[]{By.className("fusion-page-title-subtitle")}, "ROLE", LawyerExceptions::roleException);

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

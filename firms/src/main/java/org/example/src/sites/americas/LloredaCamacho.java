package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class LloredaCamacho extends ByPage {

    public LloredaCamacho() {
        super(
                "Lloreda Camacho & Co",
                "https://lloredacamacho.com/en/partners/",
                2
        );
    }

    String currentRole = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://lloredacamacho.com/en/directors/";
        String url = index == 0 ? this.link : otherUrl;
        currentRole = index == 0 ? "Partner" : "Director";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("article.elementor-post"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("elementor-heading-title"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-heading-title")}, "NAME", LawyerExceptions::nameException),
                "role", currentRole,
                "firm", this.name,
                "country", "Colombia",
                "practice_area", "",
                "email", socials[0],
                "phone", "576016511010"
        );
    }
}

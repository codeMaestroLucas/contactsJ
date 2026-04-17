package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Chediak extends ByNewPage {

    public Chediak() {
        super(
                "Chediak",
                "https://chediak.com.br/en/profissionais",
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
            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"tipo-1\"]"));
            return div.findElements(By.cssSelector(".col.mb-5 a"));
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
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("nome")}, "NAME", "textContent", LawyerExceptions::nameException);
        String practice = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("areas")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("info"));

        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "552135436100" : socials[1]
        );
    }
}

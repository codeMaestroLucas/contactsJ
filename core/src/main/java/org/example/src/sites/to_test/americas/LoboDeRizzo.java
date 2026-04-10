package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class LoboDeRizzo extends ByNewPage {

    public LoboDeRizzo() {
        super(
                "Lobo de Rizzo",
                "https://www.ldr.com.br/en/people/",
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
            return MyDriver.wait.findElements(By.cssSelector("article.block"));
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement content = driver.findElement(By.cssSelector("div.col-span-12"));

        String[] socials = super.getSocials(content.findElements(By.tagName("svg")), true);
        // Note: Using container for socials as elements are within list items with SVGs
        String email = extractor.extractLawyerText(content, new By[]{By.xpath(".//li[contains(.,'@')]")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(content, new By[]{By.xpath(".//li[contains(.,'+55')]")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", extractor.extractLawyerText(content, new By[]{By.cssSelector("p.paragraph-xs")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", email,
                "phone", phone
        );
    }
}

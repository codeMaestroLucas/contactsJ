package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class JLegal extends ByNewPage {

    public JLegal() {
        super(
                "J+Legal",
                "https://jlegal.pt/en/equipa/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("a.membro"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{}, false);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String nameOnList = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.id("equipa"));

        String rawText = extractor.extractLawyerAttribute(container, new By[]{By.className("conteudo")}, "SOCIALS", "innerHTML", LawyerExceptions::socialsException);
        String[] socials = this.getSocialsFromText(rawText);

        return Map.of(
                "link", link,
                "name", nameOnList,
                "role", "Lawyer",
                "firm", this.name,
                "country", "Portugal",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath("//h3[text()='Expertise']/following-sibling::p")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "351218770000" : socials[1]
        );
    }
}

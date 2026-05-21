package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class LavellePartners extends ByNewPage {

    public LavellePartners() {
        super(
                "Lavelle Partners",
                "https://www.lavellepartners.ie/our-people",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("article.group"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("span")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        // Extraindo dados da primeira página
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("span.uppercase!")}, "ROLE", LawyerExceptions::roleException);
        String practiceArea = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("span.md\\:text-\\[16px\\]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        // Transição para a segunda página
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.cssSelector("div.mt-\\[40px\\].flex"));

        // Extração de contatos na subpágina
        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Ireland",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35316445800" : socials[1]
        );
    }
}

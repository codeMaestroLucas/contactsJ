package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class RamonYCajalAbogados extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public RamonYCajalAbogados() {
        super(
                "Ramón y Cajal Abogados",
                "https://www.ramonycajalabogados.com/es/Abogados",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("views-row"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("field-content")}, false);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".views-field-title a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("views-field-title")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("content"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("field-name-field-cargo")}, "ROLE", LawyerExceptions::roleException);
        String vcardHref = container.findElement(By.partialLinkText("vCard")).getAttribute("href");
        String[] socials = vCard.getSocials(vcardHref);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Spain",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("field-name-field-areas")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0].isEmpty() ? extractor.extractLawyerAttribute(container, new By[]{By.cssSelector(".social a[href^='mailto:']")}, "EMAIL", "href", LawyerExceptions::emailException).replace("mailto:", "") : socials[0],
                "phone", socials[1].isEmpty() ? "34915761900" : socials[1]
        );
    }
}

package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class SantosNetoAdvogados extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public SantosNetoAdvogados() {
        super(
                "Santos Neto Advogados",
                "https://santosneto.com.br/home-en/advogados-en/",
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
        return MyDriver.wait.findElements(By.cssSelector("div.jet-listing-grid__item"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.jet-listing-dynamic-link__link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("jet-listing-dynamic-field__content")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.cssSelector("div.elementor-element-6fed3da"));

        String vcardHref = container.findElement(By.cssSelector("a.jet-listing-dynamic-link__link")).getAttribute("href");
        String[] socials = vCard.getSocials(vcardHref);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Lawyer",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0].isEmpty() ? extractor.extractLawyerText(container, new By[]{By.cssSelector(".elementor-element-aa7f7fd .jet-listing-dynamic-field__content")}, "EMAIL", LawyerExceptions::emailException) : socials[0],
                "phone", socials[1].isEmpty() ? "551131243000" : socials[1]
        );
    }
}

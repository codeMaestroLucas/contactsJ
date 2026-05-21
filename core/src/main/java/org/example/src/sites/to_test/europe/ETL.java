package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class ETL extends ByNewPage {

    public ETL() {
        super(
                "ETL",
                "https://etl.es/en/partners/",
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
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.jet-engine-listing-overlay-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("jet-listing-dynamic-field__content")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("kt-inside-inner-col"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("area-servicio")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = this.getSocials(container.findElements(By.cssSelector("a.jet-listing-dynamic-link__link")), false);
        String phone = extractor.extractLawyerText(container, new By[]{By.xpath("//p[contains(text(), 'Teléfono:')]/parent::div/following-sibling::div")}, "PHONE", (e) -> null);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Spain",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath("//p[contains(text(), 'Especialidad:')]/parent::div/following-sibling::div")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", phone.isEmpty() ? "34915194332" : phone
        );
    }
}

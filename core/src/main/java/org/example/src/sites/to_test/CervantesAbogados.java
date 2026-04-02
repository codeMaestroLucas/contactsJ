package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class CervantesAbogados extends ByNewPage {

    public CervantesAbogados() {
        super(
                "Cervantes Abogados",
                "https://cervantesabogados.mx/lawyers/?lang=en",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("elementor-cta"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("elementor-cta__description")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.xpath(".")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-cta__title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-cta__description")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("elementor-column-wrap"));
        String email = extractor.extractLawyerText(container, new By[]{By.xpath(".//h2/a[contains(@href, 'mailto:')]")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(container, new By[]{By.xpath(".//h2/a[contains(@href, 'tel:')]")}, "PHONE", LawyerExceptions::phoneException);
        String practice = extractor.extractLawyerText(container, new By[]{By.className("elementor-text-editor")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", practice,
                "email", email,
                "phone", phone.isEmpty() ? "525591785044" : phone
        );
    }
}

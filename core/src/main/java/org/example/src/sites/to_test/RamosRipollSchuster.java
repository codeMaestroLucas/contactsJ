package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class RamosRipollSchuster extends ByPage {

    public RamosRipollSchuster() {
        super(
                "Ramos, Ripoll & Schuster",
                "https://www.rrs.com.mx/english/equipo.php?id=18",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.xpath("//td[@bgcolor='#f5f5f5']"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("fecha_noticia")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("titulo_noticia")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("fecha_noticia")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//p[text()='E-mail']/following-sibling::span")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", this.link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", "",
                "email", email,
                "phone", "523336424444"
        );
    }
}

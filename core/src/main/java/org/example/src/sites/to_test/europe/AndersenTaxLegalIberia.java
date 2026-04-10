package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class AndersenTaxLegalIberia extends ByPage {

    public AndersenTaxLegalIberia() {
        super(
                "Andersen Tax & Legal Iberia",
                "https://pt.andersen.com/lawyers/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("li.data.row"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("perfil-nome")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("perfil-cargo")}, "ROLE", LawyerExceptions::roleException);
        String country = extractor.extractLawyerText(lawyer, new By[]{By.className("perfil-vcard")}, "COUNTRY", LawyerExceptions::countryException);
        String practiceArea = extractor.extractLawyerText(lawyer, new By[]{By.className("perfil-areas")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".perfil-nome a")}, "LINK", "href", LawyerExceptions::linkException);

        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "351213511120" : socials[1]
        );
    }
}

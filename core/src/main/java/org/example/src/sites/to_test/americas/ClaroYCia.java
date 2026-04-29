package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class ClaroYCia extends ByNewPage {

    public ClaroYCia() {
        super(
                "CLARO & CIA",
                "https://www.claro.cl/abogados/",
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
        return MyDriver.wait.findElements(By.cssSelector("ul.listadoAbogados li"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String firstName = extractor.extractLawyerText(lawyer, new By[]{By.className("nombre")}, "NAME", LawyerExceptions::nameException);
        String lastName = extractor.extractLawyerText(lawyer, new By[]{By.className("apellido")}, "NAME", LawyerExceptions::nameException);
        String name = firstName + " " + lastName;

        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("tipo")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("infoAbogado"));

        String[] socials = this.getSocials(container.findElements(By.cssSelector("a.telefono, a.email")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Chile",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "56223673000" : socials[1]
        );
    }
}

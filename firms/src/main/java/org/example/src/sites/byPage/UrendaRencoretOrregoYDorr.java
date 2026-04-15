package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UrendaRencoretOrregoYDorr extends ByPage {

    public UrendaRencoretOrregoYDorr() {
        super(
                "Urenda Rencoret Orrego y Dörr",
                "",
                12
        );
    }

    String[] links = {
            "https://www.urod.cl/nuestro-equipo/juan-carlos-dorr-bulnes/",
            "https://www.urod.cl/nuestro-equipo/juan-carlos-dorr-zegers/",
            "https://www.urod.cl/nuestro-equipo/gonzalo-errazuriz-h/",
            "https://www.urod.cl/nuestro-equipo/nicholas-mocarquer/",
            "https://www.urod.cl/nuestro-equipo/juan-pablo-morales-b/",
            "https://www.urod.cl/nuestro-equipo/sergio-orrego/",
            "https://www.urod.cl/nuestro-equipo/alberto-rencoret-p/",
            "https://www.urod.cl/nuestro-equipo/felipe-rencoret-portales/",
            "https://www.urod.cl/nuestro-equipo/gonzalo-rencoret-p/",
            "https://www.urod.cl/nuestro-equipo/rafael-rencoret-p/",
            "https://www.urod.cl/nuestro-equipo/francisco-urenda-p/",
            "https://www.urod.cl/nuestro-equipo/ignacio-urrutia-c/"
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(links[index]);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            return Collections.singletonList(MyDriver.wait.findElement(By.className("grve-column-wrapper")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        WebElement div = driver.findElement(By.tagName("body"));
        String name = extractor.extractLawyerText(div, new By[]{By.className("grve-title")}, "NAME", LawyerExceptions::nameException);
        String[] socials = super.getSocials(div.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Chile",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "56224995500" : socials[1]
        );
    }
}

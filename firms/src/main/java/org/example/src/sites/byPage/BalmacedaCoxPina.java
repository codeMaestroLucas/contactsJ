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

public class BalmacedaCoxPina extends ByPage {

    public BalmacedaCoxPina() {
        super("Balmaceda, Cox & Piña", "https://bcp.cl/equipo/", 7);
    }

    String currentRole = "Partner";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(links[index]);
        MyDriver.waitForPageToLoad();
        if (index > 2) currentRole = "Director";
    }

    String[] links = {
            "https://bcp.cl/matias-balmaceda-mahns/",
            "https://bcp.cl/francisco-cox-vial/",
            "https://bcp.cl/juan-ignacio-pina-rochefort/",

            "https://bcp.cl/guillermo-cantin-hein/",
            "https://bcp.cl/isidora-eyzaguirre-bas/",
            "https://bcp.cl/ivan-millan-gutierrez/",
            "https://bcp.cl/mariella-pirozzi-pfingsthorn/"
    };

    @Override
    protected List<WebElement> getLawyersInPage() {
        return Collections.singletonList(MyDriver.wait.findElement(By.cssSelector("div.elementor-widget-wrap.elementor-element-populated")));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.xpath("//div/div/section[2]/div/div[1]/div/div[2]/div/h5")}, "NAME", LawyerExceptions::nameException),
                "role", currentRole,
                "firm", this.name,
                "country", "Chile",
                "practice_area", "",
                "email", extractor.extractLawyerText(lawyer, new By[]{By.xpath("//div/div/section[2]/div/div[1]/div/div[4]/div/p/a")}, "EMAIL", LawyerExceptions::emailException).replace("Correo:", ""),
                "phone",  "56223340051"
        );
    }
}

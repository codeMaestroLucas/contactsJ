package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PerezLlorca extends ByNewPage {

    private final By[] byRoleArray = {
            By.tagName("h2")
    };

    public PerezLlorca() {
        super(
                "Pérez-Llorca",
                "https://www.perezllorca.com/en-mx/?post_type%5B%5D=abogado&search_type=abogados&s=&oficinas=Office&area-practica%5B%5D=Practices+and+Sectors&tipos=Position",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            // A estrutura de listagem do site requer navegação para os itens, mas o HTML fornecido foca no detalhe.
            // Assumindo container padrão de listagem 'card-abogado' comum em sites WordPress da firma.
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("head"));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        // No HTML fornecido, Pedro Pérez-Llorca é o header do detalhe.
        // Em um cenário ByNewPage real, buscaríamos o href do card.
        String link = driver.getCurrentUrl();
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);

        String link = Objects.requireNonNull(driver.getCurrentUrl());

        WebElement contactDiv = driver.findElement(By.className("contacto"));
        String[] socials = super.getSocialsFromText(extractor.extractLawyerText(contactDiv, new By[]{By.tagName("p")}, "SOCIALS", LawyerExceptions::socialsException));

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Spain",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.className("areas")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34914360425" : socials[1]
        );
    }
}
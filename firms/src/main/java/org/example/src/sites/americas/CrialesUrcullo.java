package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class CrialesUrcullo extends ByNewPage {

    private final By[] byRoleArray = {By.className("card-text")};

    public CrialesUrcullo() {
        super(
                "Criales Urcullo",
                "https://bolivialaw.com/equipo",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    private final String[] validRoles = {"socio", "socia", "auditor", "auditora"};

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("click-detalle"));
            return siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, this.validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("data-url");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("card-title")}, "NAME", LawyerExceptions::nameException);
        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.className("col-md-7"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", "----",
                "firm", this.name,
                "country", "Bolivia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "59122775656" : socials[1]
        );
    }
}

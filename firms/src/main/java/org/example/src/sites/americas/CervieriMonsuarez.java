package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class CervieriMonsuarez extends ByNewPage {

    public CervieriMonsuarez() {
        super(
                "Cervieri Monsuarez",
                "https://cervierimonsuarez.com/quienes-somos/",
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
        String[] validRoles = {"socio", "socia", "gerente", "partner", "director", "directora", "associado senior", "associada senior"};
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("teamItem"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = lawyer.getAttribute("data-name");
        String country = lawyer.getAttribute("data-country");
        String role = lawyer.findElement(By.className("team-member-position")).getText();

        String profileLink = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("team-card-content"));

        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", profileLink,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}

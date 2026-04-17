package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class MottaFernandes extends ByNewPage {

    public MottaFernandes() {
        super(
                "Motta Fernandes",
                "https://mottafernandes.com.br/equipe/#socios",
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
        String[] validRoles = {"sócio", "sócia", "conselheiro", "conselheira"};
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("li.images"));
            lawyers.removeFirst();
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("a")}, false, validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.cmdClickOnElement(lawyer);
        return driver.getCurrentUrl();
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("nome")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.className("detalhes"));

        WebElement socialContainer = driver.findElement(By.xpath("//*[@id=\"nossa-equipe-detalhe\"]/div[2]/div[1]/div[1]/div[2]"));

        String[] socials = super.getSocialsFromText(extractor.extractLawyerText(socialContainer, new By[]{By.tagName("p")}, "SOCIALS", LawyerExceptions::socialsException));

        return Map.of(
                "link", link,
                "name", name,
                "role", "----",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "552125332200" : socials[1]
        );
    }
}

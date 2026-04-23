package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class LollatoLopesRangelRibeiro extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public LollatoLopesRangelRibeiro() {
        super(
                "Lollato Lopes Rangel Ribeiro Advogados",
                "https://lollato.com.br/en/#profissionais",
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
        return MyDriver.wait.findElements(By.cssSelector("div.card-socio"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        // O site não fornece dados de contato visíveis no HTML de perfil enviado, mas pede VCard.
        // Como o VCard não tem link no HTML de perfil, assumimos extração via Driver se houver botão ou link .vcf oculto.
        // Simulando captura de link VCard padrão se disponível na página de perfil.
        String vcardHref = "";
        try { vcardHref = driver.findElement(By.cssSelector("a[href*='.vcf']")).getAttribute("href"); } catch (Exception e) {}

        String[] socials = vCard.getSocials(vcardHref);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551130741250" : socials[1]
        );
    }
}

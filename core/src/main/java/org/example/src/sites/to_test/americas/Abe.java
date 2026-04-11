package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Abe extends ByNewPage {

    private final String[] validRoles = {"sócio", "sócia", "socio", "socio", "diretor","diretora", "conselheiro", "conselheira", "associado senior", "associada senior"};

    public Abe() {
        super(
                "Abe",
                "https://abeadvogados.com.br/pessoas/",
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
            return MyDriver.wait.findElements(By.className("col-md-3"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".name a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("nome-email"));
        String role = extractor.extractLawyerText(container, new By[]{By.tagName("h4")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, this.validRoles)) return "Invalid Role";
        String[] socials = super.getSocials(driver.findElements(By.cssSelector(".phone-email a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551135121332" : socials[1]
        );
    }
}

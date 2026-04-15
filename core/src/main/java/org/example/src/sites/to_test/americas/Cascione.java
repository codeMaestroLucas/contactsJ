package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Cascione extends ByNewPage {

    public Cascione() {
        super(
                "Cascione",
                "https://www.cascione.com.br/en/profissionais/?letter=all",
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
        return MyDriver.wait.findElements(By.cssSelector("p > a[href*='/profissional/']"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.tagName("article"));

        String role = extractor.extractLawyerText(container, new By[]{By.tagName("small")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String[] socials = super.getSocials(container.findElements(By.className("dados-contato-advogado")), true);

        return Map.of(
                "link", link,
                "name", extractor.extractLawyerText(container, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException).split("\n")[0].trim(),
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551131653000" : socials[1]
        );
    }
}

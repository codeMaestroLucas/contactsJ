package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Silva extends ByNewPage {

    public Silva() {
        super(
                "Silva",
                "https://silva.cl/en/nuestro_equipo",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("efecto-nuestro-equipo"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("size10")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        // Site uses JS onclick, but we can infer profile based on name in URL pattern
        String name = lawyer.findElement(By.className("size16")).getText().replace(" ", "-");
        String link = "https://silva.cl/en/perfil/" + name;
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("size16")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("size10")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement header = driver.findElement(By.id("subFila1mainPerfil"));

        String email = extractor.extractLawyerAttribute(header, new By[]{By.xpath(".//a[contains(@href, 'mailto')]")}, "EMAIL", "href", LawyerExceptions::emailException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Chile",
                "practice_area", "",
                "email", email,
                "phone", "56224447900"
        );
    }
}

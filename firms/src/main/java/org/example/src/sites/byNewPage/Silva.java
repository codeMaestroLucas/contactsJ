package org.example.src.sites.byNewPage;

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
        try {
            String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
            MyDriver.openNewTab(link);
            return link;
        } catch (Exception e) {
            MyDriver.cmdClickOnElement(lawyer);
            return driver.getCurrentUrl();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("size16")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("size10")}, "ROLE", "textContent", LawyerExceptions::roleException);

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

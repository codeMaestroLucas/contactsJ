package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MachadoAssociados extends ByPage {

    public MachadoAssociados() {
        super(
                "Machado Associados",
                "https://www.machadoassociados.com.br/en/professionals/",
                4
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.machadoassociados.com.br/en/professionals/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("article.profissional"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);
        String email = extractor.extractLawyerText(lawyer, new By[]{By.className("info-email")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(lawyer, new By[]{By.className("info-telefone")}, "PHONE", LawyerExceptions::phoneException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h2 a")}, "LINK", "href", LawyerExceptions::linkException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "551130934600" : phone
        );
    }
}

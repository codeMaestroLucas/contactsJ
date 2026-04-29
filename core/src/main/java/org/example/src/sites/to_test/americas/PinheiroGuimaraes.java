package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class PinheiroGuimaraes extends ByPage {

    public PinheiroGuimaraes() {
        super(
                "Pinheiro Guimarães",
                "https://www.pinheiroguimaraes.com.br/advogados/",
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
        return MyDriver.wait.findElements(By.className("blocos"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("title-black")}, "NAME", LawyerExceptions::nameException);
        String email = extractor.extractLawyerText(lawyer, new By[]{By.className("email")}, "EMAIL", LawyerExceptions::emailException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Lawyer",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", email,
                "phone", "552145015000"
        );
    }
}

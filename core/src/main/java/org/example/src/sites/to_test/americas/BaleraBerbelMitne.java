package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class BaleraBerbelMitne extends ByNewPage {

    public BaleraBerbelMitne() {
        super(
                "Balera, Berbel & Mitne",
                "https://balera.com.br/b-staff/",
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
            WebElement div = MyDriver.wait.findElement(By.id("socios"));
            List<WebElement> lawyers = div.findElements(By.className("et_pb_column"));
            lawyers.removeFirst();
            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        // todo fix link
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = lawyer.getText();

        String link = this.openNewTab(lawyer);

        String email = MyDriver.wait.findElement(By.xpath("//div/div/div[3]/div[2]/div[1]/div[2]/div/div[2]/h4/span")).getAttribute("textContent");


        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", email,
                "phone", "551130740520"
        );
    }
}

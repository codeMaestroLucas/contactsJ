package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MedinaGarnesAbogados extends ByNewPage {

    public MedinaGarnesAbogados() {
        super(
                "Medina Garnes Abogados",
                "https://www.mga.com.do/en/our-lawers/",
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
            return MyDriver.wait.findElements(By.className("main__nuestrosabogados-lista-box-feature"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.className("main__headerabogados-info-titulo"));
        String name = extractor.extractLawyerAttribute(container, new By[]{By.tagName("h2")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(container, new By[]{By.tagName("p")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String email = extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[]{By.xpath("//a[contains(@href, 'mailto:')]")}, "EMAIL", "textContent", LawyerExceptions::emailException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Dominican Republic",
                "practice_area", "",
                "email", email,
                "phone", "18095405401"
        );
    }
}

package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class CSMVAdvogados extends ByPage {

    public CSMVAdvogados() {
        super(
                "CSMV Advogados",
                "https://www.csmv.com.br/profissionais/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("nectar-post-grid-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("meta-excerpt")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("post-heading")}, "NAME", LawyerExceptions::nameException);
        String excerpt = extractor.extractLawyerText(lawyer, new By[]{By.className("meta-excerpt")}, "ROLE", LawyerExceptions::roleException);
        
        String email = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".meta-excerpt a")}, "EMAIL", "href", LawyerExceptions::emailException);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("bg-wrap-link")}, "LINK", "href", LawyerExceptions::linkException),
                "name", name,
                "role", excerpt,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", email,
                "phone", "551130491000"
        );
    }
}

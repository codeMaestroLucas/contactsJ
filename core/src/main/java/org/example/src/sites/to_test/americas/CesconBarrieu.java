package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class CesconBarrieu extends ByNewPage {

    public CesconBarrieu() {
        super(
                "Cescon Barrieu",
                "https://cesconbarrieu.com.br/en/lawyers/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.e-loop-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("div.elementor-element-a3563fa span")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.findElement(By.cssSelector("div.meta-titulo a")).getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div.meta-titulo a")}, "NAME", LawyerExceptions::nameException);
        
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.cssSelector("div.elementor-element-14a2220"));

        String role = extractor.extractLawyerText(container, new By[]{By.cssSelector("div.vcard_titulo span")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerText(container, new By[]{By.cssSelector("div.vcard_email span")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(container, new By[]{By.cssSelector("div.vcard_telefone span")}, "PHONE", LawyerExceptions::phoneException);
        String practiceArea = extractor.extractLawyerText(container, new By[]{By.cssSelector("div.areas-topo span.elementor-post-info__terms-list")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", practiceArea,
                "email", email,
                "phone", phone.isEmpty() ? "551130896500" : phone
        );
    }
}

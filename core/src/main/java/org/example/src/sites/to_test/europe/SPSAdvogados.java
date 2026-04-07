package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class SPSAdvogados extends ByNewPage {

    public SPSAdvogados() {
        super(
                "SPS Advogados",
                "https://spsadvogados.com/en/equipa-2/socios/",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("tp-esg-item"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("eg-vanburen-element-2")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("eg-vanburen-element-0")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("eg-vanburen-element-0")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.id("textoequipa"));
        String role = extractor.extractLawyerText(container, new By[]{By.xpath(".//li[1]")}, "ROLE", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerText(container, new By[]{By.xpath(".//li[2]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException).replace("Practice Areas:", "").trim();
        String email = extractor.extractLawyerAttribute(driver.findElement(By.className("imagemequipa")), new By[]{By.tagName("a")}, "EMAIL", "href", LawyerExceptions::emailException).replace("mailto:", "");

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Portugal",
                "practice_area", practice,
                "email", email,
                "phone", "351213701900"
        );
    }
}

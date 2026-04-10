package org.example.src.sites.byNewPage;

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
                4
        );
    }

    String currentRole = "";
    String url = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        switch (index) {
            case 0:
                url = this.link;
                currentRole = "Partner";
                break;
            case 1:
                url = "https://sps-barrilero.com/en/equipa-2/professional-partners/";
                currentRole = "Partner";
                break;
            case 2:
                url = "https://sps-barrilero.com/en/equipa-2/managing-associates/";
                currentRole = "Managing Associate";
                break;
            case 3:
                url = "https://sps-barrilero.com/en/equipa-2/of-counsel/";
                currentRole = "Counsel";
        }

        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            return MyDriver.wait.findElements(By.cssSelector("ul.mainul > li.filterall"));
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
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("eg-vanburen-element-0")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.id("textoequipa"));
        String practice = extractor.extractLawyerAttribute(container, new By[]{By.xpath(".//li[contains(., 'Practice Areas')]")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException).trim();
        String email = extractor.extractLawyerAttribute(driver.findElement(By.className("imagemequipa")), new By[]{By.tagName("a")}, "EMAIL", "href", LawyerExceptions::emailException).replace("mailto:", "");

        return Map.of(
                "link", link,
                "name", name,
                "role", currentRole,
                "firm", this.name,
                "country", "Portugal",
                "practice_area", practice,
                "email", email,
                "phone", "351217803640"
        );
    }
}

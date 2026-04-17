package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class ClaroYCia extends ByNewPage {

    private final By[] byRoleArray = {
            By.className("tipo")
    };

    public ClaroYCia() {
        super(
                "Claro & Cía",
                "https://www.claro.cl/en/abogados/",
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.claro.cl/en/abogados/page/2/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("ul.listAbogados li"));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String firstName = extractor.extractLawyerText(lawyer, new By[]{By.className("nombre")}, "NAME", LawyerExceptions::nameException);
        String lastName = extractor.extractLawyerText(lawyer, new By[]{By.className("apellido")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement info = driver.findElement(By.className("infoAbogado"));
        String[] socials = super.getSocials(info.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", firstName + " " + lastName,
                "role", role,
                "firm", this.name,
                "country", "Chile",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "56223673000" : socials[1]
        );
    }
}

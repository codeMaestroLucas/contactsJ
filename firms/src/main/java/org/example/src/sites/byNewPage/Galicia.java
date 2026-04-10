package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Galicia extends ByNewPage {

    public Galicia() {
        super(
                "Galicia",
                "https://www.galicia.com.mx/links/en_nuestro-equipo?que=socios",
                2
        );
    }

    String currentRole = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.galicia.com.mx/links/en_nuestro-equipo?que=consejeros";
        String url = index == 0 ? this.link : otherUrl;
        currentRole = index == 0 ? "Partner" : "Counsel";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            return MyDriver.wait.findElements(By.cssSelector("a[href*='en_equipo?que=']"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("fName")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        String email = MyDriver.wait.findElement(By.cssSelector("a[href*='mailto']")).getAttribute("href");


        return Map.of(
                "link", link,
                "name", name,
                "role", currentRole,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", "",
                "email", email,
                "phone", "525559012900"
        );
    }
}

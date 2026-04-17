package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RamosRipollSchuster extends ByPage {

    public RamosRipollSchuster() {
        super(
                "Ramos, Ripoll & Schuster",
                "https://www.rrs.com.mx/english/equipo.php?id=18",
                97 // page 0 = listing (link collection), pages 1-96 = one lawyer each
        );
    }

    ArrayList<String> links = new ArrayList<>();

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
            List<WebElement> anchors = MyDriver.wait.findElements(
                    By.cssSelector("#txtHint td > a[href*='equipo.php?id=']"));
            for (WebElement el : anchors) {
                String href = el.getAttribute("href");
                if (href != null && !href.equals(this.link)) links.add(href);
            }
        } else {
            driver.get(links.get(index - 1));
            MyDriver.waitForPageToLoad();
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebElement contentArea = MyDriver.wait.findElement(
                    By.xpath("//*[@id='txtHint']/table/tbody/tr/td[3]"));
            return this.siteUtl.filterLawyersInPage(
                    List.of(contentArea), new By[]{By.className("fecha_noticia")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String currentUrl = driver.getCurrentUrl();

        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("titulo_noticia")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("fecha_noticia")}, "ROLE", LawyerExceptions::roleException);

        // Try mailto link first; fall back to span in row 8 of the profile table
        String email = "";
        try {
            WebElement emailAnchor = lawyer.findElement(By.cssSelector("a[href*='mailto:']"));
            email = emailAnchor.getAttribute("href").replace("mailto:", "").toLowerCase().trim();
        } catch (Exception ignored) {}
        if (email.isEmpty()) {
            email = extractor.extractLawyerText(lawyer,
                    new By[]{By.xpath("//*[@id=\"txtHint\"]/table/tbody/tr/td[3]/table/tbody/tr[2]/td/table/tbody/tr[8]/td[2]/span")},
                    "EMAIL", LawyerExceptions::emailException);
        }

        String pa = extractor.extractLawyerText(lawyer,
                new By[]{By.xpath("//*[@id=\"txtHint\"]/table/tbody/tr/td[3]/table/tbody/tr[2]/td/table/tbody/tr[4]/td/p/span[2]")},
                "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", currentUrl,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", pa,
                "email", email,
                "phone", "523336424444"
        );
    }
}

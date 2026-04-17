package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class GPAAdvogados extends ByNewPage {

    public GPAAdvogados() {
        super(
                "GPA Advogados",
                "https://www.gpasa.pt/en/team/",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("a.card"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("card-title")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.xpath(".")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("card-title")}, "NAME", LawyerExceptions::nameException).split("\n")[0].trim();
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("strong")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("gutter-half"));
        String email = extractor.extractLawyerText(container, new By[]{By.className("team-mail-container")}, "EMAIL", LawyerExceptions::emailException);
        String practice = extractor.extractLawyerText(container, new By[]{By.xpath(".//h3[contains(text(),'Practice Areas')]/following-sibling::p")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Portugal",
                "practice_area", practice,
                "email", email,
                "phone", "351213121550"
        );
    }
}

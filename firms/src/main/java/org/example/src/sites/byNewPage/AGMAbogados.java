package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class AGMAbogados extends ByNewPage {

    public AGMAbogados() {
        super(
                "AGM Abogados",
                "https://www.agmabogados.com/en/team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            return MyDriver.wait.findElements(By.className("team-item"));
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("card-body"));

        String role = extractor.extractLawyerText(container, new By[]{By.tagName("h6")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";
        String[] socials = super.getSocials(container.findElements(By.cssSelector(".socials-wrapper a")), false);
        String country = extractor.extractLawyerText(container, new By[]{By.className("office-item")}, "COUNTRY", LawyerExceptions::countryException).toLowerCase();

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country.contains("mexico") ? "Mexico" : "Spain",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("services-wrapper")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34934871122" : socials[1]
        );
    }
}

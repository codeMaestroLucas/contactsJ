package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class RomeroPineda extends ByPage {

    public RomeroPineda() {
        super(
                "Romero Pineda",
                "https://www.romeropineda.com/en/nuestra-firma/#nuestro-equipo",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("qodef-team-member-social-icons"));
        // Selecting common ancestor via XPath as the container has unique IDs
        List<WebElement> containers = MyDriver.wait.findElements(By.cssSelector(".qodef-grid-item.team"));
        return this.siteUtl.filterLawyersInPage(containers, new By[]{By.className("qodef-e-role")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String email = extractor.extractLawyerText(lawyer, new By[]{By.className("qodef-e-degree")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("qodef-e-title"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("qodef-e-title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("qodef-e-role")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "El Salvador",
                "practice_area", "",
                "email", email,
                "phone", "50325055555"
        );
    }
}

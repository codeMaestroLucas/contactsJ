package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class VanDoorne extends ByPage {

    public VanDoorne() {
        super(
                "Van Doorne",
                "https://www.vandoorne.com/en/our-people/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".ll-team-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        
        // Note: format is (lastName)@vandoorne.com
        String lastName = name.contains(" ") ? name.substring(name.lastIndexOf(" ") + 1).toLowerCase() : name.toLowerCase();
        String email = lastName + "@vandoorne.com";

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", name,
                "role", extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", "",
                "email", email,
                "phone", "31206789123"
        );
    }
}

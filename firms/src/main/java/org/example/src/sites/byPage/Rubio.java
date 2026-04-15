package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Rubio extends ByPage {

    public Rubio() {
        super(
                "Rubio",
                "https://www.rubio.pe/en/socios/",
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
            return MyDriver.wait.findElements(By.className("wp_content_Team"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        String email = extractor.extractLawyerText(lawyer, new By[]{By.className("email")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Peru",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", email,
                "phone", "5112156666"
        );
    }
}

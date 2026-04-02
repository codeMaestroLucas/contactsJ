package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MusatAsociatii extends ByPage {

    public MusatAsociatii() {
        super(
                "Muşat & Asociaţii",
                "https://www.musat.ro/attorneys/",
                4
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.musat.ro/attorneys/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            return MyDriver.wait.findElements(By.className("lawyer_card"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("lawyer_name")}, "NAME", LawyerExceptions::nameException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);

        String email = extractor.extractLawyerText(lawyer, new By[]{By.className("lawyer_email")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "----",
                "firm", this.name,
                "country", "Romania",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.className("lawyer_description")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", email,
                "phone", "40212025100"
        );
    }
}

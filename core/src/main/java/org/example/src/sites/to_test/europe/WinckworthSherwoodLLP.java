package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class WinckworthSherwoodLLP extends ByPage {

    public WinckworthSherwoodLLP() {
        super(
                "Winckworth Sherwood LLP",
                "https://wslaw.co.uk/people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("people-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("p")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String email = extractor.extractLawyerAttribute(lawyer, new By[]{By.name("dynamicname")}, "EMAIL", "value", LawyerExceptions::emailException);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("profile-link")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "United Kingdom",
                "practice_area", "",
                "email", email,
                "phone", extractor.extractLawyerText(lawyer, new By[]{By.className("tel-link")}, "PHONE", LawyerExceptions::phoneException)
        );
    }
}

package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class PlasBossinade extends ByPage {

    public PlasBossinade() {
        super(
                "PlasBossinade",
                "https://www.plasbossinade.nl/onze-mensen",
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
        String[] validRoles = {"notaris", "notarieel", "notariaat"};

        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("column-overview-person-detail"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, false, validRoles);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("person-card-inner")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("person")}, "NAME", LawyerExceptions::nameException),
                "role", "Notary",
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", "",
                "email", extractor.extractLawyerText(lawyer, new By[]{By.className("meta-item-email-address"), By.className("meta-item-value")}, "EMAIL", LawyerExceptions::emailException),
                "phone", extractor.extractLawyerText(lawyer, new By[]{By.className("meta-item-phone-number"), By.className("meta-item-value")}, "PHONE", LawyerExceptions::phoneException)
        );
    }
}

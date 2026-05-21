package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class SovereignGroup extends ByPage {

    public SovereignGroup() {
        super(
                "Sovereign Group",
                "https://www.sovereigngroup.com/meet-the-team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("team-item"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("slide-tag")}, true, validRoles);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("h5")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("h5")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("slide-tag")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Gibraltar",
                "practice_area", "",
                "email", "",
                "phone", "35020076173"
        );
    }
}

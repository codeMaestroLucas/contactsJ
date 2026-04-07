package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MGDalyPartners extends ByNewPage {

    public MGDalyPartners() {
        super(
                "M G Daly & Partners",
                "https://mgdalypartners.com/about/#Partners",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("sc_services_item"));
            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("sc_services_item_link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("sc_services_item_title")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("team_member_description"));
        String role = extractor.extractLawyerText(container, new By[]{By.className("team_member_position")}, "ROLE", LawyerExceptions::roleException);

        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String email = extractor.extractLawyerText(container, new By[]{By.className("team_member_details_value")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Trinidad and Tobago",
                "practice_area", "",
                "email", email,
                "phone", "18686234013"
        );
    }
}

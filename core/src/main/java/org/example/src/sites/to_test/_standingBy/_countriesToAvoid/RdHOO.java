package org.example.src.sites.to_test._standingBy._countriesToAvoid;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class RdHOO extends ByPage {

    public RdHOO() {
        super(
                "RdHOO",
                "https://rdhoo.com/contenidos/gente",
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
        String[] validRoles = {"socio", "socia", "counsel", "counsels"};
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("team-entry"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("job-title")}, true, validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("team-name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("job-title")}, "ROLE", LawyerExceptions::roleException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);

        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector(".social-icons a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Venezuela",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", "582129011111"
        );
    }
}

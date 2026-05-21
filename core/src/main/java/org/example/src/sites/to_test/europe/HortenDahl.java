package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class HortenDahl extends ByPage {

    public HortenDahl() {
        super(
                "HortenDahl",
                "https://www.hortendahl.dk/en/people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("employeecard"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("employeecard__jobtitle")}, true, validRoles);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("employeecard__name")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("employeecard__name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("employeecard__jobtitle")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Denmark",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "4533344000" : socials[1]
        );
    }
}

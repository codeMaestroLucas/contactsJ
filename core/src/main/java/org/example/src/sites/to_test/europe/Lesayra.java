package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Lesayra extends ByPage {

    public Lesayra() {
        super(
                "Lesayra",
                "https://www.lesayra.com/en/team",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("col-profile"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("role1-profile")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("name-profile")}, "NAME", LawyerExceptions::nameException);
        
        lawyer.click();
        WebElement container = MyDriver.wait.findElement(By.className("datos"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("cargo")}, "ROLE", LawyerExceptions::roleException);
        String practiceArea = extractor.extractLawyerText(container, new By[]{By.className("role2-profile")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", this.link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Spain",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34910052182" : socials[1]
        );
    }
}

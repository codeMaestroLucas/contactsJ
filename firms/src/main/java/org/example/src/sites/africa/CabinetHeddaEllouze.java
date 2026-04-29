package org.example.src.sites.africa;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CabinetHeddaEllouze extends ByPage {

    public CabinetHeddaEllouze() {
        super(
                "Cabinet Hedda-Ellouze Ellouze & Belajouza-Felli",
                "",
                4
        );
    }

    String[] links = {
            "/en/donia-hedda-ellouze",
            "/en/meriem-belajouza-felli",
            "/en/molka-ellouze-kraiem",
            "/en/jamila-toubal",
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get("https://www.cabinetheddaellouze.com/" + links[index]);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("div.equipe_spec"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocialsFromText(lawyer.getText());

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h2.title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("field-name-field-fonction")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Tunisia",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.className("custom_extrafield_4")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "21671230133" : socials[1]
        );
    }
}

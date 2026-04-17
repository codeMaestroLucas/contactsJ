package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PalmaLaw extends ByPage {

    public PalmaLaw() {
        super(
                "Palma Law",
                "https://www.palma.cl/en/",
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
            return MyDriver.wait.findElements(By.className("perfil"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h2")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("cargo")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("grupo")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);

        String[] socials = super.getSocialsFromText(extractor.extractLawyerAttribute(lawyer, new By[]{By.className("meta")}, "SOCIALS", "textContent", LawyerExceptions::socialsException));

        return Map.of(
                "link", "https://www.palma.cl/en/",
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Chile",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "56223676500" : socials[1]
        );
    }
}

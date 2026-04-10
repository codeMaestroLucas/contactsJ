package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class GElíasMuñoz extends ByPage {

    public GElíasMuñoz() {
        super(
                "G.Elías & Muñoz",
                "https://www.eliasymunozabogados.com/en/team",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("equipo"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("team-position")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("link-all")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("team-position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Spain",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34917608645" : socials[1]
        );
    }
}
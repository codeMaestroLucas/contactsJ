package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class CruzRoqueSemiao extends ByPage {

    public CruzRoqueSemiao() {
        super(
                "Cruz Roque Semião & Associados",
                "https://crs-advogados.com/en/our-team/",
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
        return MyDriver.wait.findElements(By.className("sc_team_item"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("sc_team_item_link")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("sc_team_item_title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("sc_team_item_subtitle")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Portugal",
                "practice_area", "",
                "email", socials[0],
                "phone", "351213192450"
        );
    }
}

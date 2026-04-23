package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class LopezRibadeneiraMora extends ByPage {

    public LopezRibadeneiraMora() {
        super(
                "López Ribadeneira Mora",
                "https://www.lopezribadeneira.com/es/equipo",
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
        String[] validRoles = {"socio", "socia"};
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".uk-card-body"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true, validRoles);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("el-title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("el-meta")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Ecuador",
                "practice_area", "",
                "email", socials[0],
                "phone", "59323932910"
        );
    }
}

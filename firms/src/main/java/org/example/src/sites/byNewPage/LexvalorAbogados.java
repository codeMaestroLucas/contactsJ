package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class LexvalorAbogados extends ByNewPage {

    private final By[] byRoleArray = {
            By.className("brxe-code")
    };

    public LexvalorAbogados() {
        super(
                "Lexvalor Abogados",
                "https://lexvalor.com/en/team/",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".brxe-pjlrgu.brxe-div"));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.brxe-gagnec")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("brxe-hfaxnk")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.id("brxe-olgcwm"));
        String[] socials = super.getSocials(container.findElements(By.cssSelector(".brxe-social-icons a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Ecuador",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("areas_de_practica")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "59323230375" : socials[1]
        );
    }
}

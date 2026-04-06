package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MalagaSolicitors extends ByNewPage {

    public MalagaSolicitors() {
        super(
                "Malaga Solicitors",
                "https://www.malagasolicitors.com/about-us/",
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
            return MyDriver.getINSTANCE().findElements(By.className("gdlr-core-personnel-list"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("gdlr-core-personnel-list-title")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);


        WebElement container = driver.findElement(By.className("gdlr-core-pbf-column-content"));
        String role = this.getRole(container);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = super.getSocialsFromText(driver.findElement(By.xpath("//*[@id=\"attorna-page-wrapper\"]/div/div[3]/div[2]/div/div[2]/div/div/div[2]/div/div")).getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", "Solicitor",
                "firm", this.name,
                "country", "Spain",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34952901225" : socials[1]
        );
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String role = extractor.extractLawyerText(lawyer, new By[] {By.xpath("//*[@id=\"attorna-page-wrapper\"]/div/div[2]/div[2]/div/div[2]/div/div[2]/div[1]/div/span")}, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }
}

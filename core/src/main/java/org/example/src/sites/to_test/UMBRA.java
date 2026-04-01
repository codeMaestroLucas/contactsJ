package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class UMBRA extends ByNewPage {

    public UMBRA() {
        super(
                "UMBRA",
                "https://umbra.law/lawyers/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = driver.findElements(By.className("as-wposslide"));
        return lawyers; // Roles are deep in the profile
    }

    private String getRole(WebElement content) throws LawyerExceptions {
        String role = extractor.extractLawyerText(content, new By[]{By.tagName("em")}, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("vc_col-sm-4"));

        String role = this.getRole(container);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String name = extractor.extractLawyerText(container, new By[]{By.tagName("strong")}, "NAME", LawyerExceptions::nameException);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String phone = super.getSocialsFromText(container.getText())[1];

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Indonesia",
                "practice_area", "",
                "email", socials[0],
                "phone", phone.isEmpty() ? "622150820999" : phone
        );
    }
}

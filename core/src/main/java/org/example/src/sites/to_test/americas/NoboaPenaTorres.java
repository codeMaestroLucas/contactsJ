package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class NoboaPenaTorres extends ByNewPage {

    public NoboaPenaTorres() {
        super(
                "Noboa, Peña & Torres",
                "https://www.legalecuador.com/en/team/",
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
            return MyDriver.wait.findElements(By.className("lawyer"));
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

    private String getRole(WebElement container) throws LawyerExceptions {
        String role = extractor.extractLawyerText(container, new By[]{By.className("elementor-widget-text-editor")}, "ROLE", LawyerExceptions::roleException);
        boolean valid = siteUtl.isValidPosition(role, validRoles);
        return valid ? role : "Invalid Role";
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("card-title")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement contactInfo = driver.findElement(By.className("column-data"));
        String role = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.className("lawyer-position")}, "ROLE", LawyerExceptions::roleException);

        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String[] socials = super.getSocials(contactInfo.findElements(By.tagName("a")), false);
        String phone = extractor.extractLawyerText(contactInfo, new By[]{By.className("tel")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Ecuador",
                "practice_area", "",
                "email", socials[0],
                "phone", phone.isEmpty() ? "59322970193" : phone
        );
    }
}

package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PNMAdovagos extends ByNewPage {

    public PNMAdovagos() {
        super(
                "PNM Adovagos",
                "https://www.pnm.adv.br/lawyers/?lang=en",
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
        try {
            return MyDriver.wait.findElements(By.className("vcex-post-type-entry"));
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
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("vcex-post-type-entry-title")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        String container = driver.findElement(By.className("ptb_col1-1")).getText();

        String role = extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[]{By.xpath("//*[@id=\"main\"]/header/div/span/span")}, "ROLE", "textContent", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String[] socials = super.getSocialsFromText(container);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", container,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551136387000" : socials[1]
        );
    }
}

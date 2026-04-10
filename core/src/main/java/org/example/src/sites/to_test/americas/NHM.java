package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class NHM extends ByNewPage {

    public NHM() {
        super(
                "NHM",
                "https://nhmadv.com.br/en/partnres/",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("nhm-card"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("info")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("title")}, "NAME", LawyerExceptions::nameException);
        String roleInfo = extractor.extractLawyerText(lawyer, new By[]{By.className("info")}, "ROLE", LawyerExceptions::roleException);
        String role = roleInfo.split("\\|")[0].trim();

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.className("nhm-team-single"));

        String[] socials = super.getSocials(container.findElements(By.className("item")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath(".//p[contains(text(),'Areas of Expertise:')]/following-sibling::div")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551130784000" : socials[1]
        );
    }
}

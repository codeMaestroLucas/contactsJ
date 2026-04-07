package org.example.src.sites.to_test.asia;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class JuliusCreasy extends ByNewPage {

    public JuliusCreasy() {
        super(
                "Julius & Creasy",
                "https://www.juliusandcreasy.com/our-people",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("search_result_block"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("search_result_position")}, true);
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("search_result_name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("search_result_position")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("row"));
        String[] socials = super.getSocialsFromText(container.getText());
        String practice = extractor.extractLawyerText(container, new By[]{By.xpath(".//div[text()='Areas of Practice']/following-sibling::div")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Sri Lanka",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "94112422601" : socials[1]
        );
    }
}

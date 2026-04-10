package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class HDLegal extends ByNewPage {

    public HDLegal() {
        super(
                "HD Legal",
                "https://www.hdgroup.cl/legal/en/our-team/",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("li > a[data-item]"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("persona-term")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = lawyer.getAttribute("textContent");
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("persona-term")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.className("persona-meta"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Chile",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "56224053200" : socials[1]
        );
    }
}

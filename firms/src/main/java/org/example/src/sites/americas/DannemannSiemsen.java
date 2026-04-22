package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class DannemannSiemsen extends ByNewPage {

    public DannemannSiemsen() {
        super(
                "Dannemann Siemsen",
                "https://www.dannemann.com.br/time.html?&lang=en",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("li.team-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("team-level")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("team-name")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("team-level")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        Thread.sleep(1000);
        WebElement container = MyDriver.wait.findElement(By.className("director"));
        String[] socials = super.getSocialsFromText(container.getAttribute("innerHTML"));if (socials[1].isEmpty()) socials[1] = container.findElement(By.className("phone")).getText();

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "552122378956" : socials[1]
        );
    }
}

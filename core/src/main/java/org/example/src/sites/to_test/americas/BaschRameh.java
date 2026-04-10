package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class BaschRameh extends ByNewPage {

    public BaschRameh() {
        super(
                "Basch & Rameh",
                "https://baschrameh.com.br/en/partners-and-associates/",
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
            return MyDriver.wait.findElements(By.className("ind"));
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h5")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("info"));
        String role = extractor.extractLawyerText(container, new By[]{By.tagName("h4")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551130648045" : socials[1]
        );
    }
}

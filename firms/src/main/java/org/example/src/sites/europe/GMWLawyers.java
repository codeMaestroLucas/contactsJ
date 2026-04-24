package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class GMWLawyers extends ByNewPage {

    public GMWLawyers() {
        super(
                "GMW lawyers",
                "https://www.gmw.nl/en/lawyers/",
                4
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.gmw.nl/en/lawyers/?_paged=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.className("lawyer-card"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("overlay-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("lawyer-name")}, "NAME", LawyerExceptions::nameException);
        String practice = lawyer.getText();

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("contact-info"));

        String role = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.className("function")}, "PRACTICE", LawyerExceptions::practiceAreaException).replace(name, "").trim();
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";


        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "310703615048" : socials[1]
        );
    }
}

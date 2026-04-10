package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class LSWF extends ByNewPage {

    public LSWF() {
        super(
                "LSWF",
                "https://www.lswf.de/en/team/",
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
            return MyDriver.wait.findElements(By.cssSelector("article.post-type-page"));
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement content = driver.findElement(By.className("wp-block-column"));

        String role = extractor.extractLawyerText(content, new By[]{By.tagName("h1")}, "ROLE", LawyerExceptions::roleException).split("\n")[1];
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";
        String[] socials = super.getSocials(content.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Germany",
                "practice_area", extractor.extractLawyerText(content, new By[]{By.cssSelector("p.no-margin-bottom")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "494030085100" : socials[1]
        );
    }
}

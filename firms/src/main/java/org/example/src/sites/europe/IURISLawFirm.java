package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class IURISLawFirm extends ByNewPage {

    public IURISLawFirm() {
        super(
                "IURIS Law Firm",
                "https://iurismalta.com/about-us/#about_us_lawyers",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.et_pb_column_1_4"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.et_pb_button")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h4.et_pb_module_header")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("p.et_pb_member_position")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.tagName("body"));

        String email = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href^='mailto:']")}, "EMAIL", "href", LawyerExceptions::emailException).replace("mailto:", "");
        String practiceArea = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href*='http://iurismalta.com/practice-areas/']")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Malta",
                "practice_area", practiceArea,
                "email", email,
                "phone", "35621225578"
        );
    }
}

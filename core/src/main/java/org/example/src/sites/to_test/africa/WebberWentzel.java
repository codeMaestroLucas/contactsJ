package org.example.src.sites.to_test.africa;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class WebberWentzel extends ByPage {

    public WebberWentzel() {
        super(
                "Webber Wentzel",
                "https://www.webberwentzel.com/Specialists/Pages/default.aspx",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.SpecialistResult"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("Title")}, true);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("ProfileButton")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("Name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("Title")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "South Africa",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "27115305000" : socials[1]
        );
    }
}

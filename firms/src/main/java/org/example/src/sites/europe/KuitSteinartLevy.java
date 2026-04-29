package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class KuitSteinartLevy extends ByPage {

    public KuitSteinartLevy() {
        super(
                "Kuit Steinart Levy",
                "https://www.kuits.com/meet-the-team/",
                4
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.kuits.com/meet-the-team/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.col-12.col-md-6.col-lg-6"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("fw-normal")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector(".contactActions a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3"), By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3")}, "NAME", "textContent", LawyerExceptions::nameException),
                "role", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("fw-normal")}, "ROLE", "textContent", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "England",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "441619126359" : socials[1]
        );
    }
}

package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class LupicinioInternational extends ByNewPage {

    public LupicinioInternational() {
        super(
                "Lupicinio International",
                "https://lupicinio.com/en/the-team/",
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
        return MyDriver.wait.findElements(By.cssSelector("li.lawyer-list-item"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".image-wrapper span")}, "NAME", LawyerExceptions::nameException);
        String practiceArea = extractor.extractLawyerText(lawyer, new By[]{By.className("lawyer-areas")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("lawyer_h_info"));

        String role = extractor.extractLawyerText(container, new By[]{By.xpath(".//div[contains(text(),'POSITION')]/following-sibling::text()[1]")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = this.getSocialsFromText(container.getAttribute("innerText"));

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Spain",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34914360090" : socials[1]
        );
    }
}

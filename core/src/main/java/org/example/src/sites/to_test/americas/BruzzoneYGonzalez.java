package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BruzzoneYGonzalez extends ByNewPage {

    public BruzzoneYGonzalez() {
        super(
                "Bruzzone & González",
                "https://bruzzoneygonzalez.cl/en/our-team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("et_pb_column_1_3"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.cssSelector("p")}, true);
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("p")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("//*[@id='site-main']/div/div/div[1]/div[1]/div[2]"));
        String email = extractor.extractLawyerText(container, new By[]{By.xpath(".//span[contains(text(), '@')]")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Chile",
                "practice_area", "",
                "email", email,
                "phone", "xxxxxx"
        );
    }
}

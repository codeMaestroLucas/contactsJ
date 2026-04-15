package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class HVGLaw extends ByNewPage {

    public HVGLaw() {
        super(
                "HVG Law",
                "https://www.hvglaw.nl/en/our-people/",
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
        return MyDriver.wait.findElements(By.cssSelector("div.hvg-block.employees"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("block-title")}, "NAME", LawyerExceptions::nameException);
        String area = extractor.extractLawyerText(lawyer, new By[]{By.className("block-subtitle")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("container"));

        String[] socials = super.getSocials(container.findElements(By.cssSelector("a.btn")), true);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Lawyer",
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", area,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31884070400" : socials[1]
        );
    }
}

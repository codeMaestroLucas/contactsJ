package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MannBenham extends ByNewPage {

    public MannBenham() {
        super(
                "MannBenham",
                "https://mannbenham.com/about-mannbenham/our-people/",
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
        return MyDriver.wait.findElements(By.className("fusion-portfolio-post"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("entry-title")}, "NAME", LawyerExceptions::nameException);
        
        String link = this.openNewTab(lawyer.findElement(By.className("fusion-rollover-link")));
        WebElement container = MyDriver.wait.findElement(By.className("project-info"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String practiceArea = extractor.extractLawyerText(container, new By[]{By.className("project-terms")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Lawyer",
                "firm", this.name,
                "country", "Isle of Man",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "4401624639350" : socials[1]
        );
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }
}

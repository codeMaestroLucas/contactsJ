package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class MeritsTree extends ByNewPage {

    public MeritsTree() {
        super(
                "Merits & Tree",
                "http://www.meritsandtree.com/en/personnel/",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".personnel_list li")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("p2")}, true);
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("p1")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("p2")}, "ROLE", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerText(lawyer, new By[]{By.className("col_2")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("details_info"));
        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "China",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "861056500900" : socials[1]
        );
    }
}

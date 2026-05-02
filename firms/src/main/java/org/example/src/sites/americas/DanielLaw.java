package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DanielLaw extends ByNewPage {

    public DanielLaw() {
        super(
                "Daniel Law",
                "https://www.daniel-ip.com/en/team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.scrollToBottom(0.5);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"post-85787\"]/div/div/div/div[2]"));
            return div.findElements(By.cssSelector(".et_pb_team_member"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.cmdClickOnElement(lawyer);
        return driver.getCurrentUrl();
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.tagName("body"));

        String name = extractor.extractLawyerText(container, new By[]{By.xpath("//*[@id=\"main-content\"]/div/div/div[2]/div[1]/div[1]/div[1]/div")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.xpath("//*[@id=\"main-content\"]/div/div/div[2]/div[1]/div[1]/div[2]/div")}, "ROLE", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerText(container, new By[]{By.xpath("//*[@id=\"main-content\"]/div/div/div[2]/div[1]/div[1]/div[7]/div")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String email = driver.findElement(By.xpath("//*[@id=\"main-content\"]/div/div/div[2]/div[1]/div[1]/div[4]/a")).getAttribute("href");

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", practice,
                "email", email,
                "phone", "552121024212"
        );
    }
}

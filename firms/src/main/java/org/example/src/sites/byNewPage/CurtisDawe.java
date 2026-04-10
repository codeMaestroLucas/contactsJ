package org.example.src.sites.byNewPage;

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

public class CurtisDawe extends ByNewPage {

    public CurtisDawe() {
        super(
                "Curtis Dawe",
                "https://curtisdawe.com/index.php/our-team/",
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
        By[] byRoleArray = {By.className("et_pb_member_position")};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("et_pb_team_member")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);
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
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[] {By.cssSelector("h4.et_pb_module_header")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[] {By.cssSelector("p.et_pb_member_position")}, "ROLE", LawyerExceptions::roleException);
        String pa = extractor.extractLawyerText(lawyer, new By[] {By.cssSelector("div.et_pb_team_member_description div > p")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//div/div/div/div[1]/div[3]/div"));

        String[] socials = super.getSocials(container.findElements(By.cssSelector("h4 > span")), true);
        socials[0] = container.findElement(By.cssSelector("a[href*='@curtisdawe.co']")).getAttribute("href");

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Canada",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "7097225181" : socials[1]
        );
    }
}

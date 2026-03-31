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
import java.util.Objects;

public class ERM extends ByNewPage {

    public ERM() {
        super(
                "ERM",
                "https://erm-law.com/en/team/",
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://erm-law.com/en/team?page=2";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));

            WebElement until = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"_main\"]/section/div/div[1]")));
            List<WebElement> lawyers = until.findElements(By.cssSelector("div.block.h-full"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("capitalize")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div.italic.text-2xl")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("capitalize")}, "ROLE", LawyerExceptions::roleException);
        String pa = lawyer.getAttribute("textContent");

        String link = this.openNewTab(lawyer);
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
        WebElement container = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//section[1]/div/div[2]")));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Israel",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "97236061600" : socials[1]
        );
    }
}

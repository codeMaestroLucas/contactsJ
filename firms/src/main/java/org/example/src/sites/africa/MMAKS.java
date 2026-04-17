package org.example.src.sites.africa;

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

public class MMAKS extends ByNewPage {

    public MMAKS() {
        super(
                "MMAKS",
                "https://www.mmaks.co.ug/who-we-are/team",
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

            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[2]/main/article/section/div/div[1]/div")));
            return div.findElements(By.cssSelector("a.staff-view-mode--listing[href*='/who-we-are/team/']"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = lawyer.getAttribute("title");

        this.openNewTab(lawyer);
        List<WebElement> p = driver.findElements(By.cssSelector("div.contacts > p"));
        String[] socials = super.getSocials(p, true);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Uganda",
                "practice_area", "",
                "email", socials[0],
                "phone", "256393260017"
        );
    }
}

package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class KeystoneLaw extends ByNewPage {
    private final By[] byRoleArray = {
            By.tagName("p")
    };

    public KeystoneLaw() {
        super(
            "Keystone Law",
            "https://www.keystonelaw.com/lawyers",
            27,
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://keystonelaw.com/lawyers/?_paged=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) MyDriver.clickOnAddBtn(By.className("cky-btn-accept"));
        MyDriver.scrollToBottom(0.5);

    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"main\"]/div[1]/div[1]")));
            List<WebElement> lawyers = div.findElements(By.cssSelector("a[href*='https://keystonelaw.com/lawyers/']"));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.cmdClickOnElement(lawyer);
        return driver.getCurrentUrl();
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("h1")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("p")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }


    private String getPracticeArea() {
        return driver.findElement(By.xpath("//*[@id=\"main\"]/div[1]/div[2]/div[2]/p[1]")).getAttribute("textContent");
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);

        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
        WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"main\"]/div[1]/div[1]/div/div[2]")));

        String[] socials = super.getSocialsFromText(div.getAttribute("textContent"));

        return Map.of(
                "link", link,
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "England",
                "practice_area", this.getPracticeArea(),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "2033193700" : socials[1]
        );
    }
}

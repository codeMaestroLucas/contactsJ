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

public class ShookLinAndBokNP extends ByNewPage {

    public ShookLinAndBokNP() {
        super(
                "Shook Lin & Bok",
                "http://shooklin.com.my/lawyers/partners/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("ul.slb-partners-list li > a")
                    )
            );
        } catch (Exception e) {
            return this.driver.findElements(By.cssSelector("li:has(.slb-partnertests-card) > a"));
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        try {
            return lawyer.findElement(By.tagName("h6")).getAttribute("textContent");
        } catch (Exception e) {
            throw LawyerExceptions.nameException("Name not found via data-image-title");
        }
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        try {
            return driver.findElement(By.xpath("//h2[contains(text(),'Practice Areas')]/ancestor::div[contains(@class,'cortana-heading')]/following-sibling::div//p")).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            WebElement contactDiv = driver.findElement(By.xpath("//h2[contains(text(),'Contact')]/ancestor::div[contains(@class,'cortana-heading')]/following-sibling::div"));
            List<WebElement> links = contactDiv.findElements(By.tagName("a"));
            return super.getSocials(links, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String nName = this.getName(lawyer);
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("wpb_wrapper"));
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", nName,
                "role", "Partner",
                "firm", this.name,
                "country", "Malaysia",
                "practice_area", this.getPracticeArea(container),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "60320311788" : socials[1]
        );
    }
}

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

public class HaidermotaAndCo extends ByNewPage {

    public HaidermotaAndCo() {
        super(
                "Haidermota & Co",
                "https://www.hmco.com.pk/people/",
                1
        );
    }

    private final String[] otherUrls = {
            "",
            "https://www.hmco.com.pk/managing-associates/",
            "https://www.hmco.com.pk/senior-associates/"
    };
    private final String[] roles = {
            "Partner",
            "Managing Associate",
            "Senior Associate"
    };

    private String currentRole = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = otherUrls[index];
        String url = index == 0 ? this.link : otherUrl;
        currentRole = roles[index];
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a[href*='https://hmco.com.pk/team/']")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return link;
    }

    public String getName() {
        return driver.findElement(By.xpath("//div/section[1]/div[3]/div/div/div[1]/div/h2")).getAttribute("textContent");
    }

    public String getRole() {
        return driver.findElement(By.xpath("//div/section[1]/div[3]/div/div/div[2]/div/h2")).getAttribute("textContent");
    }

    public String getPracticeArea() {
        return driver.findElement(By.xpath("//div/section[2]/div/div[2]/div")).getAttribute("textContent");
    }

    private String[] getSocials() {
        String textContent = driver
                .findElement(By.xpath("//div/section[2]/div/div[1]/div/div[2]/div/ul"))
                .getAttribute("textContent");

        assert textContent != null;

        textContent = textContent
                .replaceAll("\\n", "")
                .replaceAll("\\t", "")
                .trim();

        return textContent.split("\\+");
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(),
                "role", currentRole,
                "firm", this.name,
                "country", "Pakistan",
                "practice_area", this.getPracticeArea(),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "92021111520000" : socials[1]
        );
    }
}
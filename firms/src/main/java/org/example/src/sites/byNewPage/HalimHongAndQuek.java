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

public class HalimHongAndQuek extends ByNewPage {

    public HalimHongAndQuek() {
        super(
                "Halim Hong & Quek",
                "https://hhq.com.my/partners/",
                1
        );
    }


    private final String[] otherUrls = {
            "",
            "https://hhq.com.my/team-principal-associates/",
            "https://hhq.com.my/team-senior-associates/"
    };
    private final String[] roles = {
            "Partner",
            "Principal Associate",
            "Senior Associate"
    };

    private String currentRole = "";


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        currentRole = roles[index];
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("elementor-cta")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    public String getName() {
        try {
            return driver.findElement(By.xpath("/html/body/div[2]/section[2]/div/div[2]/div/div[2]/div/h2")).getAttribute("textContent");
        } catch (Exception e) {
            return driver.findElement(By.xpath("/html/body/div[2]/section[1]/div/div[2]/div/div[2]/div/h2")).getAttribute("textContent");
        }
    }

    public String getPracticeArea() {
        return driver.findElement(By.xpath("/html/body/div[2]/section[4]/div/div/div/div[1]")).getAttribute("textContent");
    }

    private String[] getSocials() {
        try {
            String innerText = driver.findElement(By.xpath("/html/body/div[2]/section[2]/div/div[2]/div/div[5]/div")).getAttribute("innerText");
            assert innerText != null;
            return super.getSocialsFromText(innerText);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
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
                "country", "Malaysia",
                "practice_area", this.getPracticeArea(),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "60327103818" : socials[1]
        );
    }
}
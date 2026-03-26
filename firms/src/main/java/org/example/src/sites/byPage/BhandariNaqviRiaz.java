package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class BhandariNaqviRiaz extends ByPage {

    private final By[] byRoleArray = {
            By.cssSelector("h5.person_title span")
    };

    public BhandariNaqviRiaz() {
        super(
                "Bhandari Naqvi Riaz",
                "https://bnrlaw.net/our-people/",
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
        String[] validRoles = {"partner", "counsel", "associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"content\"]/article/div/div[3]"))
            );

            List<WebElement> lawyers = div.findElements(By.cssSelector("div.wpb_wrapper.vc_column-inner"));

            div = driver.findElement(By.xpath("//*[@id=\"content\"]/article/div/div[5]"));
            lawyers.addAll(div.findElements(By.cssSelector("div.wpb_wrapper.vc_column-inner")));

            div = driver.findElement(By.xpath("//*[@id=\"content\"]/article/div/div[7]"));
            lawyers.addAll(div.findElements(By.cssSelector("div.wpb_wrapper.vc_column-inner")));

            div = driver.findElement(By.xpath("//*[@id=\"content\"]/article/div/div[9]"));
            lawyers.addAll(div.findElements(By.cssSelector("div.wpb_wrapper.vc_column-inner")));

            div = driver.findElement(By.xpath("//*[@id=\"content\"]/article/div/div[10]"));
            lawyers.addAll(div.findElements(By.cssSelector("div.wpb_wrapper.vc_column-inner")));

            return lawyers;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        try {
            By[] byArray = {By.cssSelector("a.btn")};
            return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        } catch (LawyerExceptions e) {
            return "Invalid Role";
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("person_title")};
        String fullText = extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
        return fullText.split("\n")[0].trim();
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h5.person_title:nth-of-type(2)")};
        return extractor.extractLawyerText(lawyer, byArray, "EMAIL", LawyerExceptions::emailException);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String mLink = this.getLink(lawyer);
        if (mLink.equals("Invalid Role") || mLink.isBlank()) return "Invalid Role";

        return Map.of(
                "link", mLink,
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Pakistan",
                "practice_area", "",
                "email", this.getEmail(lawyer),
                "phone", "924235775141"
        );
    }
}

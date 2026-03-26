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

public class Cerraloglu extends ByNewPage {

    public Cerraloglu() {
        super(
                "Cerrahoğlu",
                "https://cerrahoglu.av.tr/en/our-team/",
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
        By[] byRoleArray = {By.className("elementor-widget-text-editor")};
        String[] validRoles = {"counsel", "partner", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"post-988892\"]/div/div/section[2]/div/div/div")
                    )
            );
            List<WebElement> lawyers = div.findElements(By.className("elementor-widget-wrap"));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    public String getName() {
        return driver.findElement(By.xpath("//div/section/div/div[1]/div/div[1]/div/h1")).getAttribute("textContent");
    }

    private String getRole()  {
        return driver.findElement(By.xpath("//div/section/div/div[1]/div/div[2]/div")).getAttribute("textContent");
    }

    private String[] getSocials() {
        String pa = "";
        String email = driver.findElement(By.xpath("//div/section/div/div[1]/div/div[3]/div/div/section[1]/div/div/div/section[1]/div/div[2]/div/div/div/div/a")).getAttribute("href");
        try {
            pa = driver.findElement(By.xpath("//div/section/div/div[1]/div/div[3]/div/div/section[1]/div/div/div/div[3]/div/p[1]")).getAttribute("textContent");
        } catch (Exception e) {}

        return new String[]{email, "902122664400", pa};
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(),
                "role", this.getRole(),
                "firm", this.name,
                "country", "Turkey",
                "practice_area", socials[2],
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
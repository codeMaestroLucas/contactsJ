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

public class PerchstoneAndGraeys extends ByNewPage {

    private final By[] byRoleArray = {By.tagName("p")};


    public PerchstoneAndGraeys() {
        super(
                "Perchstone & Graeys",
                "https://perchstoneandgraeys.com/teams/",
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
        String[] validRoles = {"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));

            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[2]/div/div/div")));
            List<WebElement> lawyers = div.findElements(By.cssSelector("a[href*='https://perchstoneandgraeys.com/']"));

            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    public String getName() {
        return driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div[2]/div[2]/div/h2")).getAttribute("textContent");
    }

    private String getRole() {
        return driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div[2]/div[1]")).getAttribute("textContent").trim();
    }

    private String[] getSocials() {
        try {
            String text = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div[2]/div[3]/div")).getAttribute("textContent");
            return super.getSocialsFromText(text);
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
                "role", this.getRole(),
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "2342013429131" : socials[1]
        );
    }
}

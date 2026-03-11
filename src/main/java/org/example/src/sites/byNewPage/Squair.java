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

public class Squair extends ByNewPage {

    public Squair() {
        super(
                "Squair",
                "https://www.squairlaw.com/en/notre-equipe",
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.squairlaw.com/en/notre-equipe?cd2d96e7_page=2";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.w-dyn-items div.lawyer-item")));
            lawyers.removeFirst();
            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("a.w-inline-block[href*='/en/avocats/']")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    public String getName() {
        try {
            return driver.findElement(By.xpath("//*[@id=\"page-wrap\"]/main/div/div/div[2]/div[1]/div/div[1]")).getAttribute("textContent");
        } catch (Exception e) {
            return "";
        }
    }

    private String getRole() {
        return driver.findElement(By.xpath("//*[@id=\"page-wrap\"]/main/div/div/div[2]/div[2]/div[1]")).getAttribute("textContent");
    }

    private String getPA() {
        return driver.findElement(By.cssSelector("a[href*='/en/expertises/']")).getAttribute("textContent");
    }

    private String[] getSocials() {
        try {
            String email = driver.findElement(By.xpath("//*[@id=\"page-wrap\"]/main/div/div/div[2]/div[2]/div[3]/div[2]/div[1]/div[2]/div")).getAttribute("textContent");
            String phone = driver.findElement(By.xpath("//*[@id=\"page-wrap\"]/main/div/div/div[2]/div[2]/div[3]/div[2]/div[2]/div[2]/div")).getAttribute("textContent");
            return new String[]{email, phone};
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
                "country", "France",
                "practice_area", this.getPA(),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "330181695960" : socials[1]
        );
    }
}

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

public class McCulloughRobertson extends ByNewPage {

    private final By[] byRoleArray = {By.className("e-con-inner")};


    public McCulloughRobertson() {
        super(
                "McCullough Robertson",
                "https://mccullough.com.au/people/",
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
            String[] validRoles = {"partner", "counsel", "director", "senior associate"};

            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"content\"]/div/div/div[3]/div/div/div[1]/div/div[1]"))
            );
            List<WebElement> lawyers = div.findElements(By.xpath("//*[@id=\"content\"]/div/div/div[3]/div/div/div[1]/div/div[1]/div"));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("a[href*='https://mccullough.com.au/staff_members/']")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    public String getName() {
        By[] byArray = {By.tagName("h1")};
        try {
            return extractor.extractLawyerText(driver.findElement(By.tagName("body")), byArray, "NAME", LawyerExceptions::nameException);
        } catch (LawyerExceptions e) {
            throw new RuntimeException(e);
        }
    }

    private String getRole() throws LawyerExceptions {
        By[] byArray = {By.tagName("h2")};
        return extractor.extractLawyerText(driver.findElement(By.tagName("body")), byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement container) {
        String email = "";
        String phone = "";
        try {
            email = container.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href");
            phone = container.findElement(By.xpath(".//div[contains(., 'T:')]")).getText();
        } catch (Exception ignored) {}
        return new String[]{email, phone};
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("elementor-element-a63ea73"));

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(),
                "role", this.getRole(),
                "firm", this.name,
                "country", "Australia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}

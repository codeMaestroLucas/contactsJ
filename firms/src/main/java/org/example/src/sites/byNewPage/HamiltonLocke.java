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

public class HamiltonLocke extends ByNewPage {

    private final By[] byRoleArray = {
            By.cssSelector(".elementor-element-57eddd2 .elementor-heading-title")
    };

    public HamiltonLocke() {
        super(
                "Hamilton Locke",
                "https://hamiltonlocke.com.au/our-team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1500L);
        MyDriver.clickOnElementMultipleTimes(By.id("load-more"), 4, 1);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "chairman", "counsel", "director", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(15L));


            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"team-listing\"]/div/div/div"))
            );
            List<WebElement> lawyers = div.findElements(By.cssSelector("section.elementor-element-ea00324"));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyers on Hamilton Locke", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".elementor-element-8f03d72 a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getNName() {
        return driver.findElement(By.xpath("//*[@id=\"name-section\"]/div/div[1]/div/div[1]/div/h1")).getAttribute("textContent");
    }

    private String getRole() {
        return driver.findElement(By.xpath("//*[@id=\"name-section\"]/div/div[1]/div/div[2]")).getAttribute("textContent");
    }

    private String[] getSocials(WebElement container) {
        try {
            String email = container.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href").replace("mailto:", "");
            String phone = container.findElement(By.cssSelector("a[href^='tel:']")).getAttribute("href").replace("tel:", "");
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    private String getPracticeArea(WebElement container) {
        try {
            By[] byArray = {By.className("jet-listing-dynamic-terms")};
            return extractor.extractLawyerText(container, byArray, "PRACTICE", LawyerExceptions::practiceAreaException);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("elementor-element-eac5d94"));
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getNName(),
                "role", this.getRole(),
                "firm", this.name,
                "country", "Australia",
                "practice_area", this.getPracticeArea(container),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}

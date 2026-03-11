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

public class AnJieBroad extends ByNewPage {

    public AnJieBroad() {
        super(
                "AnJie Broad",
                "https://www.anjielaw.com/team/team.html?isclock=1",
                1
        );
    }

    private final By[] byRoleArray = {
            By.className("cpt")
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.clickOnElement(By.xpath("//*[@id=\"main-header\"]/div/div/div[4]/ul/li[3]/div/button[1]"));
        Thread.sleep(3000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            String[] validRoles = {"partner", "counsel",  "senior associate"};

            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a[href*='./resume.html?id=']")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
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

    private String getName(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, new By[]{By.id("lawyer_name")}, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, new By[]{By.id("lawyer_job")}, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[]{By.xpath("//span[text()='Office：']/following-sibling::span")}, "COUNTRY", LawyerExceptions::countryException);
        return country.equalsIgnoreCase("hong kong") ? "Hong Kong" : "China";
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String email = lawyer.findElement(By.xpath("//span[text()='Email：']/following-sibling::span")).getAttribute("textContent");
            String phone = lawyer.findElement(By.xpath("//span[text()='Tel：']/following-sibling::span")).getAttribute("textContent");
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("headcontent"));
        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "861085675906" : socials[1]
        );
    }
}

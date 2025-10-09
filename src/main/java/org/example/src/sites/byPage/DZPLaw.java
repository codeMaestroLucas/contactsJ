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

public class DZPLaw extends ByPage {

    private final By[] byRoleArray = {
            By.cssSelector("p.font-semibold")
    };

    public DZPLaw() {
        super(
                "DZP Law",
                "https://www.dzp.pl/en/choose-adviser?name=&position%5B%5D=1&position%5B%5D=8&position%5B%5D=3&position%5B%5D=2",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.clickOnAddBtn(By.id("cookies_accept"));
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.col-span-9.grid")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{By.cssSelector("h3 > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("h3")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String email = lawyer.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href");
            String phone = lawyer.findElement(By.cssSelector("div.col-span-9.lg\\:col-span-4 > p:nth-of-type(2)")).getAttribute("textContent");
            return new String[]{email, phone};
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Poland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "48225577600" : socials[1]
        );
    }
}
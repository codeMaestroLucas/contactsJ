package org.example.src.sites.byPage;

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

public class SoemadipradjaAndTaher extends ByNewPage {

    private final By[] byRoleArray = {By.cssSelector("p:last-child")};

    public SoemadipradjaAndTaher() {
        super(
                "Soemadipradja & Taher",
                "https://www.soemadipradjataher.com/people",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        } else {
            MyDriver.clickOnElement(By.xpath("//*[@id=\"lawyers\"]/div[3]/div[3]"));
            MyDriver.waitForPageToLoad();
            Thread.sleep(2000);
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("a.cursor-pointer[href*='/people/']")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Error finding lawyers", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("p.text-32")};
        return extractor.extractLawyerAttribute(div, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("p.mt-12")};
        return extractor.extractLawyerAttribute(div, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement div) {
        try {
            String email = div.findElement(By.cssSelector("a[href^='mailto:']")).getText();
            return new String[]{email, ""};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("left-people-details-card"));
        String[] socials = getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", getName(div),
                "role", getRole(div),
                "firm", this.name,
                "country", "Indonesia",
                "practice_area", "",
                "email", socials[0],
                "phone", "62215220545"
        );
    }
}

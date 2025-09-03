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

public class WikborgRein extends ByNewPage {
    private final By[] byRoleArray = {
            By.className(""),
            By.cssSelector("")
    };

    public WikborgRein() {
        super(
            "WikborgRein",
            "https://www.wr.no/en/people",
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
//        MyDriver.clickOnElement(By.id(""));
        MyDriver.rollDown(8, 0.3); //todo: check
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[href^='']")//todo
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("mb-10"),
                By.cssSelector("h1")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className(""),
                By.cssSelector("")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className(""),
                By.cssSelector("")
        };
        return extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
    }


    private String[] getSocials(WebElement lawyer) {
        String email = ""; String phone = "";

        WebElement socialsDiv = driver.findElement(By.xpath("//*[@id=\"main-content\"]/section/div/div/div[2]/div[2]/div/div[3]/div[1]/"));
        email = socialsDiv.findElement(By.cssSelector("a[href*='mailto:']")).getAttribute("href");
        phone = socialsDiv.findElement(By.cssSelector("div > div:nth-of-type(2)")).getText();

        return new String[] { email, phone };
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className(""));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Norway",
                "practice_area", this.getCountry(div),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}

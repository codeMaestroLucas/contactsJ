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

public class GilbertAndTobin extends ByNewPage {

    private final By[] byRoleArray = {
            By.cssSelector(".card__metadata .body-default:first-child")
    };

    public GilbertAndTobin() {
        super(
                "Gilbert + Tobin",
                "https://www.gtlaw.com.au/our-people?query=&start_rank=1&sort=metasortKey&num_ranks=16",
                11
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.gtlaw.com.au/our-people?query=&start_rank=" + ((16 * index) + 1) + "&sort=metasortKey&num_ranks=16";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(2000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(15L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".card--profile"))
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyers on Gilbert + Tobin", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement container) throws LawyerExceptions {
        By[] byArray = {By.className("hero-banner__name")};
        return extractor.extractLawyerText(container, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement container) throws LawyerExceptions {
        By[] byArray = {By.className("metadata-block__value")};
        return extractor.extractLawyerText(container, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials() {
        WebElement div = driver.findElement(By.xpath("//*[@id=\"heroBannerContacts\"]"));
        List<WebElement> socials = div.findElements(By.tagName("a"));
        return super.getSocials(socials, false);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        try {
            MyDriver.clickOnElement(By.cssSelector(".hero-banner__toggle button"));
            Thread.sleep(500);
        } catch (Exception ignored) {}

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement container = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("hero-banner__content")));

        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", this.getRole(container),
                "firm", this.name,
                "country", "Australia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
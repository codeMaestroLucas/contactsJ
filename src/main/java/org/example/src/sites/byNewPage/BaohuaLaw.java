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

public class BaohuaLaw extends ByNewPage {

    public BaohuaLaw() {
        super(
                "Baohua Law",
                "https://www.baohualaw.com/team.html",
                1
        );
    }

    private final By[] byRoleArray = {
            By.tagName("div")
    };


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElement(By.xpath("//*[@id=\"bs-example-navbar-collapse-1\"]/ul[2]/li[2]/a"));
        Thread.sleep(3000);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            String[] validRoles = {"partner", "counsel",  "senior associate"};

            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.col-md-3[data-target]")));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a.team-link")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getNameFromList(WebElement lawyer) throws LawyerExceptions {
        String text = lawyer.getText();
        if (text.contains("/")) {
            return text.split("/")[0].trim();
        }
        return text.trim();
    }

    private String getRoleFromList(WebElement lawyer) {
        String text = lawyer.getText();
        if (text.contains("/")) {
            return text.split("/")[1].trim();
        }
        return "----";
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> links = lawyer.findElements(By.tagName("a"));
            return super.getSocials(links, true);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getNameFromList(lawyer);
        String role = this.getRoleFromList(lawyer);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("list_team2"));
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "China",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "8603262323979" : socials[1]
        );
    }
}
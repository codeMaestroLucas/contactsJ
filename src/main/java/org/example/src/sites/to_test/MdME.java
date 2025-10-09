package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MdME extends ByNewPage {

    By[] webRole = {
            By.className("card-body"),
            By.className("card-subtitle")
    };

    public MdME() {
        super(
                "MdME",
                "https://www.mdme.com/en/our-people/",
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
        String[] validRoles = {"partner", "senior associate", "counsel"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("card-team-container")
                    )
            );
            return siteUtl.filterLawyersInPage(lawyers, webRole, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a.card-team")};
        String href = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        String url = href.startsWith("http") ? href : "https://www.mdme.com" + href;
        MyDriver.openNewTab(url);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.page-title h1")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.page-title h2")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) {
        return lawyer
                .findElement(By.cssSelector("div.share-container a[href*='/en/locations/']"))
                .getAttribute("textContent");
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            return lawyer
                    .findElement(By.xpath("//h3[contains(text(), 'Expertise')]/following-sibling::ul/li"))
                    .getAttribute("textContent");
        } catch (Exception e) {
            return "";
        }
    }


    private String constructEmail(String name) {
        if (name == null || name.isEmpty()) return "";
        name = TreatLawyerParams.treatName(name)
                .toLowerCase().trim();
        return name.replace(" ", ".") + "@mdme.com";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.cssSelector("div.row.g-4.g-lg-5"));

        String name = this.getName(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", this.getRole(container),
                "firm", this.name,
                "country", this.getCountry(container).replace(",", ""),
                "practice_area", this.getPracticeArea(container),
                "email", this.constructEmail(name),
                "phone", ""
        );
    }
}

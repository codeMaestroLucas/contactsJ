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

public class HamsoPatentybra extends ByNewPage {
    private final By[] byRoleArray = {
            By.cssSelector("div.brxe-inifnc.brxe-text > p")
    };

    public HamsoPatentybra() {
        super(
            "Hamso Patentybra",
            "https://www.patent.no/en/our-employees/",
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
        String[] validRoles = new String[]{
                "partner",
                "advisor",
                "head"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("brxe-ggujxa")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
            By.cssSelector("a[href^='https://www.patent.no/en/medarbeidere/']")
        };
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href",LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h4")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME","textContent", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("brxe-text-basic")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("brxe-block"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.id("brxe-uuobks"));

        String[] socials = this.getSocials(div);

        String role = this.getRole(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", role,
                "firm", this.name,
                "country", "Norway",
                "practice_area", role.toLowerCase().contains("patent") ? "Patents" : "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}

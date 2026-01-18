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

public class ChevezRuizZamarripa extends ByPage {
    private final By[] byRoleArray = {
            By.tagName("h3")
    };

    public ChevezRuizZamarripa() {
        super(
                "Chevez Ruiz Zamarripa",
                "https://www.chevez.com/En/Contacts",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1500L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.row.socios div.col-lg-3")
                    )
            );
            lawyers.addAll(driver.findElements(By.cssSelector("div.row.directores div.col-lg-3")));
            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='Profile']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h3")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) {
        // Checking image path for role hints
        try {
            String imgSrc = lawyer.findElement(By.tagName("img")).getAttribute("src");
            if (imgSrc.contains("socios")) return "Partner";
        } catch (Exception ignored) {}

        return "Lawyer";
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            String att = lawyer.findElement(By.tagName("p")).getAttribute("innerHTML");
            phone = att.split("<br>")[0];
            email = att.split("<br>")[1];
            return new String[]{email, phone};
        } catch (Exception e) {
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
                "country", "Mexico",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "525552577000" : socials[1]
        );
    }
}

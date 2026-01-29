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

public class ZBV extends ByNewPage {
    private final By[] byRoleArrayListing = {
            By.className("text-block-12")
    };

    public ZBV() {
        super(
                "ZBV",
                "https://www.zbv.com.ar/eng/team-eng",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement partnersDiv = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/section[1]/div[2]/div[1]/div[2]/div/div"))
            );
            return partnersDiv.findElements(By.cssSelector("div[role='listitem']"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("link-block-4")};
        String profileLink = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(profileLink);
        return profileLink;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("heading-33")};
        return extractor.extractLawyerText(driver.findElement(By.className("div-block-70")), byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement container) {
        try {
            List<WebElement> links = container.findElements(By.tagName("div"));
            String email = "";
            String phone = "";
            for (WebElement el : links) {
                String text = el.getText();
                if (text.contains("@")) email = text;
                else if (text.matches(".*\\d{5,}.*")) phone = text;
            }
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("div-block-70"));
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(),
                "role", "Partner",
                "firm", this.name,
                "country", "Argentina",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "541143234000" : socials[1]
        );
    }
}

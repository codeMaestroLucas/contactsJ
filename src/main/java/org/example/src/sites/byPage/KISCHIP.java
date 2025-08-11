package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KISCHIP extends ByPage {
    public KISCHIP() {
        super(
            "KISCH IP",
            "https://www.kisch-ip.com/Directors",
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.kisch-ip.com/SeniorAssociates";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        // Click on add btn
        MyDriver.clickOnElement(By.className("accept"));
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("MemberBlock")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("MemberName")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole() {
        return Objects.requireNonNull(
                driver.getCurrentUrl()).toLowerCase().contains("director")
                ? "Director" : "Senior Associate";
    }


    private String getCountry(String phone) {
        return phone
                .replaceFirst("\\+", "")
                .startsWith("27")
                ? "South Africa" : "Jersey";
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
            "link", this.getLink(lawyer),
            "name", this.getName(lawyer),
            "role", this.getRole(),
            "firm", this.name,
            "country", this.getCountry(socials[1]),
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1]
        );
    }
}

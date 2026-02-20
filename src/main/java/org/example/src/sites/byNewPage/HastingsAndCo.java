package org.example.src.sites.byNewPage;

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

public class HastingsAndCo extends ByNewPage {

    public HastingsAndCo() {
        super(
                "Hastings And Co",
                "https://hastings-hk.com/en/people.php?wid=247",
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://hastings-hk.com/en/people.php?wid=247&cid=39";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("people-box-wrapper")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("people-box-title")};
        String text = extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
        return text.split("\\(")[0].trim();
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("p")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer, String name) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("ul li a"));
            String[] socials1 = super.getSocials(socials, true);

            if (socials1[0].isEmpty()) {
                // Some emails are empty
                String[] parts = TreatLawyerParams.treatNameForEmail(name).split(" ");
                socials1[0] = parts[0] + parts[parts.length - 1] + "@hastings-hk.com";
            }

            return socials1;
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("people-details-box-text"));
        String name1 = this.getName(div);
        String[] socials = this.getSocials(div, name1);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name1,
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Hong Kong",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "85225239161" : socials[1]
        );
    }
}

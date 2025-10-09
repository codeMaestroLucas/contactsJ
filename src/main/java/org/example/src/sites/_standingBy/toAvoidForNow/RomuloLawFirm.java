package org.example.src.sites._standingBy.toAvoidForNow;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RomuloLawFirm extends ByPage {

    public RomuloLawFirm() {
        super(
                "Romulo Law Firm",
                "https://www.romulo.com/all/",
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
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.col.span_12.dark.left")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.xpath(".//a[contains(text(), 'View Full Profile')]")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("p > span[style*='font-weight: bold']")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.xpath(".//p[2]")};
        String text = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        if (text.toLowerCase().contains("partner")) {
            return "Partner";
        }
        return text;
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            WebElement emailElement = lawyer.findElement(By.xpath(".//*[contains(text(), 'Primary Email:')]/span"));
            email = siteUtl.getContentFromTag(emailElement.getAttribute("outerHTML"));
        } catch (Exception ignored) {}
        try {
            String text = lawyer.findElement(By.xpath(".//*[contains(text(), 'DID 1:')]")).getText();
            Matcher matcher = Pattern.compile("DID 1: ([^D]+)").matcher(text);
            if (matcher.find()) {
                phone = matcher.group(1).trim();
            }
        } catch (Exception ignored) {}
        return new String[]{email, phone};
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "the Philippines",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}
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

public class RahmatLimAndPartners extends ByPage {

    public RahmatLimAndPartners() {
        super(
                "Rahmat Lim And Partners",
                "https://www.rahmatlim.com/partners/",
                4
        );
    }

    @Override
    protected void accessPage(int index) {
        String otherUrl = "https://www.rahmatlim.com/partners/?page=" + (index + 1) + "&region=2692&";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
        return wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("card-body"))
        );
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.heading a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.heading a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("div.addr-item a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    private String getPhone(WebElement lawyer) {
        try {
            return lawyer.findElement(By.className("tel")).getText();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "Partner",
                "firm", this.name,
                "country", "Malaysia",
                "practice_area", "",
                "email", socials[0],
                "phone", this.getPhone(lawyer)
        );
    }
}
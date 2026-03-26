package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ASAndHCliffordChance extends ByPage {

    public ASAndHCliffordChance() {
        super(
                "AS&H Clifford Chance",
                "https://www.ashcliffordchance.com/en/lawyers.html",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("article_aside_contact")));
            WebElement div = driver.findElement(By.xpath("//*[@id=\"nav_secondary\"]/ul/li[2]/ul"));
            lawyers.addAll(div.findElements(By.tagName("li")));
            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer profile article", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        return lawyer.findElement(By.cssSelector("a[href*='https://www.ashcliffordchance.com/en/lawyers/']")).getAttribute("href");
    }

    private String getName(WebElement lawyer)  {
        try {
             return lawyer.findElement(By.cssSelector("h1.pageColour_text")).getText();
        } catch (Exception e) {
            return lawyer.findElement(By.cssSelector("ul > li > a[href*='https://www.ashcliffordchance.com/en/lawyers/counsel/']")).getText();
        }
    }

    private String getPhone(WebElement lawyer) {
        try {
            return lawyer.findElement(By.className("section_profile")).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private String getEmail(String name) {
        String[] parts = TreatLawyerParams.treatNameForEmail(name).split(" ");
        if (parts.length >= 2) {
            return parts[0] + "." + parts[parts.length - 1] + "@ashcliffordchance.com";
        }
        return parts[0] + "@ashcliffordchance.com";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String email = this.getEmail(name);
        String phone = this.getPhone(lawyer);
        String mLink = this.getLink(lawyer);

        return Map.of(
                "link", mLink,
                "name", name,
                "role", mLink.toLowerCase().contains("counsel") ? "Counsel" : "Partner",
                "firm", this.name,
                "country", "Saudi Arabia",
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "966114819700" : phone
        );
    }
}

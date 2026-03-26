package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CityYuwaPartners extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("position")
    };


    public CityYuwaPartners() {
        super(
                "City-Yuwa Partners",
                "https://www.city-yuwa.com/global/en/attorneys/",
                1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.clickOnElementMultipleTimes(By.className("btn-more"), 1, 1);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner", "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement lawyersDiv = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"primary\"]/div[2]/div/div[3]/div[1]/ul[1]")
                    )
            );
            List<WebElement> lawyers = lawyersDiv.findElements(By.cssSelector("li > a"));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return null;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("text-box"),
                By.tagName("h1")
        };
        String name = extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
        String[] parts = name.split(",\\s*");
        return (parts.length > 1) ? parts[1] + " " + parts[0] : name;
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("text-box"),
                By.className("position")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }


    private @Nullable String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("round-box"),
                By.className("practice_list"),
                By.cssSelector("li:nth-child(2)")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }

    private String[] getEmail(WebElement lawyer, String name) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("div.profile-dl > dd"));
            String[] socials1 = super.getSocials(socials, true);
            if (socials1.length > 0) throw new Exception("Create email");
            return socials1;
        } catch (Exception e) {
            String[] names = TreatLawyerParams.treatNameForEmail(name).split(" ");
            String email = names[0] + "." + names[1] + "@city-yuwa.com";
            return new String[]{email, ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("content-box"));
        String name1 = this.getName(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name1,
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Japan",
                "practice_area", this.getPracticeArea(div),
                "email", this.getEmail(div, name1)[0],
                "phone", "81362125500"
        );
    }
}
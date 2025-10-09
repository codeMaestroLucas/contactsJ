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

public class TaiwanLaw extends ByPage {

    public TaiwanLaw() {
        super(
                "Taiwan Law",
                "https://www.taiwanlaw.com/en/team_list.php",
                1,
                2
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
        return wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("name_box"))
        );
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("more")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h3")};
        String name = extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
        return name.replaceAll("[\\p{IsHan}]+", "").trim();
    }

    private String buildEmail(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        String[] parts = name.toLowerCase().split(" ");
        if (parts.length < 2) {
            return "";
        }
        return parts[0] + "." + parts[1] + "@taiwanlaw.com";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Taiwan",
                "practice_area", "",
                "email", this.buildEmail(name),
                "phone", "xxxxxx"
        );
    }
}
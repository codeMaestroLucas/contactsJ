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
import java.util.Objects;

public class AbeledoGottheil extends ByPage {

    public AbeledoGottheil() {
        super(
                "Abeledo Gottheil",
                "https://abeledogottheil.com.ar/en/attorneys/partners/",
                2
        );
    }

    private String currentRole = "";

    protected void accessPage(int index) {
        String otherUrl = "https://abeledogottheil.com.ar/en/attorneys/of-counsel/";
        String url = index == 0 ? this.link : otherUrl;
        currentRole = index == 0 ? "Partner" : "Counsel";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("areaAbogado")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.text > h4 > a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(String name) throws LawyerExceptions {
        String[] nameParts = TreatLawyerParams.treatNameForEmail(name).split(" ");
        String email = nameParts[nameParts.length - 1] + "@abeledogottheil.com.ar";
        return new String[]{email, ""};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name1 = this.getName(lawyer);
        String[] socials = this.getSocials(name1);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name1,
                "role", currentRole,
                "firm", this.name,
                "country", "Argentina",
                "practice_area", "",
                "email", socials[0],
                "phone", "541145161500"
        );
    }
}
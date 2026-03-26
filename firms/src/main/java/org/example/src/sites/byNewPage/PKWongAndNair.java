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

public class PKWongAndNair extends ByNewPage {

    public PKWongAndNair() {
        super(
                "PK Wong & Nair",
                "https://pkwongnair.com/directors/",
                2
        );
    }

    private String currentRole = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://pkw.com.sg/senior-associates/";
        String url = index == 0 ? this.link : otherUrl;
        currentRole = index == 0 ? "Director" : "Senior Associate";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("lawyer_profile_list_grid")));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".cmsmasters_img_link")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("lawyer_name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String text = lawyer.findElement(By.className("contact_lawyer")).getText();
            String email = "";
            String phone = "";
            for (String line : text.split("\n")) {
                if (line.contains("Email:")) email = line.replace("Email:", "").trim();
                if (line.contains("DID:")) phone = line.replace("DID:", "").replaceAll("[^0-9+]", "");
            }
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("cmsmasters_column_inner"));
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", currentRole,
                "firm", this.name,
                "country", "Singapore",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}

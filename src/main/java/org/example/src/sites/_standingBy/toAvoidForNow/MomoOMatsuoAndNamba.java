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

public class MomoOMatsuoAndNamba extends ByPage {

    public MomoOMatsuoAndNamba() {
        super(
                "Momo-o Matsuo And Namba",
                "https://www.mmn-law.gr.jp/en/lawyers/index.html",
                1,
                15
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.lawyer-list > a")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        String href = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
        return "https://www.mmn-law.gr.jp" + href;
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("main")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String constructEmail(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        String[] nameParts = name.toLowerCase().trim().split("\\s+");
        String lastName = nameParts[nameParts.length - 1];
        return lastName + "@mmn-law.gr.jp";
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String email = this.constructEmail(name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Japan",
                "practice_area", "",
                "email", email,
                "phone", "xxxxxx"
        );
    }
}
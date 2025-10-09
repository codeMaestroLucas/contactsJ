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

public class NishimuraAndAsahi extends ByPage {

    public NishimuraAndAsahi() {
        super(
                "Nishimura And Asahi",
                "https://www.nishimura.com/en/people/list?search_keywords_all=&job_title_aggregated_field=971&field_member_office_all=All&field_member_job_language_all=All",
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
                            By.cssSelector("div.person-list-box > a")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        String href = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
        return "https://www.nishimura.com" + href;
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("h3")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String constructEmail(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        String[] nameParts = name.toLowerCase().trim().split("\\s+");
        if (nameParts.length < 2) {
            return "";
        }
        char firstNameLetter = nameParts[0].charAt(0);
        String lastName = nameParts[nameParts.length - 1];
        return firstNameLetter + "." + lastName + "@nishimura.com";
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
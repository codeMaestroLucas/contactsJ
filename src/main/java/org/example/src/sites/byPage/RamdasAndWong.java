package org.example.src.sites.byPage;

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

public class RamdasAndWong extends ByPage {
    private final By[] byRoleArray = {
            By.className("designation")
    };


    public RamdasAndWong() {
        super(
            "Ramdas And Wong",
            "https://www.ramdwong.com.sg/#lawyersprofile",
            1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("profile-open")
//                            By.cssSelector("section#lawyersprofile > div.col-12")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("name")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return siteUtl.getContentFromTag(element.getAttribute("outerHTML"));
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return siteUtl.getContentFromTag(element.getAttribute("outerHTML"));
    }


    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";

        String phoneRegex = "\\(\\d{2}\\)\\s*\\d{4}\\s*\\d{4}";
        String emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";

        Pattern phonePattern = Pattern.compile(phoneRegex);
        Pattern emailPattern = Pattern.compile(emailRegex);

        try {
            String phoneHtml = lawyer.findElement(By.className("telephone")).getAttribute("outerHTML");
            String emailHtml = lawyer.findElement(By.className("email")).getAttribute("outerHTML");

            Matcher phoneMatcher = phonePattern.matcher(phoneHtml);
            Matcher emailMatcher = emailPattern.matcher(emailHtml);

            if (phoneMatcher.find()) {
                phone = phoneMatcher.group();
            }

            if (emailMatcher.find()) {
                email = emailMatcher.group();
            }

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{ email, phone };
    }



    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
            "link", this.link,
            "name", this.getName(lawyer),
            "role", this.getRole(lawyer),
            "firm", this.name,
            "country", "Singapore",
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1]
        );
    }
}

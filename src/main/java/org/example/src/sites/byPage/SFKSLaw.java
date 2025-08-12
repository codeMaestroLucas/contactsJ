package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SFKSLaw extends ByPage {
    private List<String> links = new ArrayList<>();
    private final By[] byRoleArray = {
            By.id("MemberTitle"),
            By.cssSelector("dl > dt:nth-child(2)")
    };


    public SFKSLaw() {
        super(
            "SFKS Law",
            "https://www.sfks.com.hk/en/members/member.php?id=5",
            13
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String url = index == 0 ? this.link : this.links.get(index -1);
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) {
            List<WebElement> elements = driver.findElements(By.cssSelector("dl#menu1 > dd > a, dl#menu3 > dd > a"));
            for (WebElement element : elements) {
                this.links.add(element.getAttribute("href"));
            }
        }
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.id("mainContent")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.id("mainStatus"),
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String[] split = element.getText().split(">");
        return split[split.length - 1];
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";

        String socials = driver
                .findElement(By.id("QuickContact"))
                .findElement(By.cssSelector("p"))
                .getAttribute("outerHTML");

        // Regex to capture phone and email
        Pattern pattern = Pattern.compile("(\\d{4}\\s\\d{4})|([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})");
        Matcher matcher = pattern.matcher(socials);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                phone = matcher.group(1).trim();
            } else if (matcher.group(2) != null) {
                email = matcher.group(2).trim();
            }
        }

        return new String[]{ phone, email };
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(lawyer),
            "role", this.getRole(lawyer),
            "firm", this.name,
            "country", "Hong Kong",
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1]
        );
    }
}

package org.example.src.sites.byNewPage;

import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;

public class HuiyeLaw extends ByNewPage {
    String[] validRoles = new String[]{
            "partner", "伙伴", "高级合伙人"
    };


    // All site in Chinese
    public HuiyeLaw() {
        super(
            "Huiye Law",
            "https://www.huiyelaw.com/zyry.html",
            17
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.huiyelaw.com/Zyry/index/p/" + (index + 1) + ".html";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("rightnr")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        MyDriver.openNewTab(element.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h2.title:nth-of-type(2) > em")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("h2.title > em")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String role = element.getText();
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

//
//    private String getPracticeArea() {
//        WebElement lawyer = driver.findElement(By.className("hotlist"));
//        By[] byArray = new By[]{
//                By.cssSelector("ul > li")
//        };
//        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
//        return element.getText();
//    }


    private String[] getSocials(WebElement lawyer) {
        String email = ""; String phone = "";

        try {
            String[] text = lawyer.findElement(By.cssSelector("p"))
                    .getAttribute("outerHTML")
                    .split("<br>");

            email = text[0];
            Pattern pattern = Pattern.compile("[\\w.-]+@[\\w.-]+");
            Matcher matcher = pattern.matcher(email);

            if (matcher.find()) {
                email = matcher.group();
            }

            phone = text[1];


        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }

        return new String[] { email, phone };
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("crew-info"));

        String role = this.getRole(div);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", "Partner*****",
            "firm", this.name,
            "country", "China",
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}

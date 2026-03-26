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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HuiyeLaw extends ByNewPage {
    String[] validRoles = new String[]{
            "partner", "伙伴", "高级合伙人"
    };

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


    public String openNewTab(WebElement lawyer) {
        try {
            By[] byArray = {By.cssSelector("a")};
            String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
            MyDriver.openNewTab(link);
        } catch (LawyerExceptions e) {
            System.err.println("Failed to open new tab: " + e.getMessage());
        }
        return null;
    }

    public String getLink() {
        return driver.getCurrentUrl();
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.title:nth-of-type(2) > em")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.title > em")};
        String role = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";

        try {
            String[] text = lawyer.findElement(By.cssSelector("p"))
                    .getAttribute("outerHTML")
                    .split("<br>");

            String emailText = text[0];
            Pattern pattern = Pattern.compile("[\\w.-]+@[\\w.-]+");
            Matcher matcher = pattern.matcher(emailText);

            if (matcher.find()) {
                email = matcher.group();
            }

            if (text.length > 1) {
                phone = text[1].replaceAll("\\D", "");
            }

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }

        return new String[]{email, phone};
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("crew-info"));

        String role = this.getRole(div);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", this.getLink(),
                "name", this.getName(div),
                "role", "",
                "firm", this.name,
                "country", "China",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
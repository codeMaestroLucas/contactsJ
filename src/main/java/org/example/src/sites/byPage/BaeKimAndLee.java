package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaeKimAndLee extends ByPage {
    private String[] letters = { "", "e", "i", "o", "u", "y", "n", "g", "k", "s" };
    // S - JUST HAVE 17 PAGES

    public BaeKimAndLee() {
        super(
            "Bae Kim And Lee",
            "https://www.bkl.co.kr/law/member/allList.do?isMain=&pageIndex=1&searchCondition=&url=all&job=&lang=en&memberNo=&searchYn=Y&logFunction=&searchKeyword=a#,1,25,1",
            10,
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = String.format("https://www.bkl.co.kr/law/member/allList.do?isMain=&pageIndex=1&searchCondition=&url=all&job=&lang=en&memberNo=&searchYn=Y&logFunction=&searchKeyword=%s#,1,25,1", letters[index]);
        String url = (index == 0) ? this.link : otherUrl;
        driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);
        MyDriver.rollDown(1, 0.5);
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        By[] webRole = {
                By.className("txt2"),
        };

        String[] validRoles = {
                "partner",
                "counsel",
                "senior associate",
                "advisor",
        };

        try {
            // Wait up to 10 seconds for elements to be present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.member-section > ul > li"))
            );

            return siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        try {
            // Get the 'onclick' attribute from the <a> tag
            String onclickValue = lawyer
                    .findElement(By.cssSelector("a"))
                    .getAttribute("onclick");

            // Match the pattern goView('123')
            Pattern pattern = Pattern.compile("goView\\('([0-9]+)'\\)");
            Matcher matcher = pattern.matcher(onclickValue);

            if (matcher.find()) {
                String number = matcher.group(1); // Extract the number
                return "https://www.bkl.co.kr/law/member/memberView.do?lang=en&memberNo="
                        + number + "&logFunction=goView";
            }

        } catch (Exception e) {
            System.err.println("Error getting link: " + e.getMessage());
        }

        return ""; // Return empty string if not found
    }


    private String getName(WebElement lawyer) {
        By[] byArray = {
                By.className("txt1"),
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = {
                By.className("txt2"),
        };
        WebElement element = siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String[] getSocials(WebElement lawyer) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        try {
            WebElement phoneElement = wait.until(driver -> lawyer.findElement(By.className("txt3")));
            WebElement emailElement = wait.until(driver -> lawyer.findElement(By.className("email")));

            String phone = phoneElement.getText();
            String email = emailElement.getAttribute("href");

            return new String[]{email, phone};

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Korea (South)",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}

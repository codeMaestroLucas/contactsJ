package org.example.src.sites.byPage;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaeKimAndLee extends ByPage {
    private final String[] letters = new String[]{"", "e", "i", "o", "u", "y", "n", "g", "k", "s"};

    public BaeKimAndLee() {
        super(
                "Bae Kim And Lee",
                "https://www.bkl.co.kr/law/member/allList.do?isMain=&pageIndex=1&searchCondition=&url=all&job=&lang=en&memberNo=&searchYn=Y&logFunction=&searchKeyword=a#,1,25,1",
                10
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = String.format("https://www.bkl.co.kr/law/member/allList.do?isMain=&pageIndex=1&searchCondition=&url=all&job=&lang=en&memberNo=&searchYn=Y&logFunction=&searchKeyword=%s#,1,25,1", this.letters[index]);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.rollDown(1, 0.5);
    }

    protected List<WebElement> getLawyersInPage() {
        By[] webRole = new By[]{
                By.className("txt2")
        };
        String[] validRoles = new String[]{"partner", "counsel", "senior associate", "advisor"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.member-section > ul > li")));
            return this.siteUtl.filterLawyersInPage(lawyers, webRole, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String getLink(WebElement lawyer) {
        try {
            String onclickValue = lawyer.findElement(By.cssSelector("a")).getAttribute("onclick");
            Pattern pattern = Pattern.compile("goView\\('([0-9]+)'\\)");
            Matcher matcher = pattern.matcher(onclickValue);
            if (matcher.find()) {
                String number = matcher.group(1);
                return "https://www.bkl.co.kr/law/member/memberView.do?lang=en&memberNo=" + number + "&logFunction=goView";
            }
        } catch (Exception e) {
            System.err.println("Error getting link: " + e.getMessage());
        }
        return "";
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("txt1")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("txt2")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String phone = lawyer.findElement(By.className("txt3")).getText();
            String email = lawyer.findElement(By.className("email")).getAttribute("href");
            return new String[]{email, phone};
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

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
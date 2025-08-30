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
import java.util.Objects;

public class LeeAndKo extends ByPage {
    private final String[] letters = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };

    private final By[] byRoleArray = {
            By.className("leeko-member-item__title"),
            By.cssSelector("p")
    };


    public LeeAndKo() {
        super(
                "Lee And Ko",
                "https://www.leeko.com/leenko/member/memberSearchResultList.do?lang=EN&pageNo=2&schKeyword=a",
                26
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.leeko.com/leenko/member/memberSearchResultList.do?lang=EN&pageNo=2&schKeyword=" + this.letters[index].toLowerCase();
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "advisor"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.leeko-member__list > a.leeko-member-item")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String getLink(WebElement lawyer) throws LawyerExceptions {
        String onclickValue = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "onclick", LawyerExceptions::linkException);
        String linkNumber = onclickValue.replaceAll("[^0-9]", "");
        return "https://www.leeko.com/leenko/member/memberDetail.do?lang=EN&memberNo=" + linkNumber + "&schReturnType=REDIRECT";
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("leeko-member-item__title"),
                By.cssSelector("strong > span")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("leeko-member-item__info"))
                    .findElements(By.cssSelector("p"));
            return super.getSocials(socials, true);
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
                "email", socials[0].replaceFirst("e", "").trim(),
                "phone", socials[1]
        );
    }
}
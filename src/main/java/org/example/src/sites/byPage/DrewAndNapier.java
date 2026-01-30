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

public class DrewAndNapier extends ByPage {
    private final By[] byRoleArray = {By.cssSelector("div.designation p")};

    public DrewAndNapier() {
        super(
                "DrewAndNapier",
                "https://www.drewnapier.com/Our-Lawyers?name=a&practice=",
                26
        );
    }

    private static final String[] LETTERS = {
            "a", "b","c","d","e","f","g","h","i","j","k","l","m",
            "n","o","p","q","r","s","t","u","v","w","x","y","z"
    };

    private int getLastPageNumber() {
        try {
            WebElement lastPageLink = driver.findElement(By.xpath("//div[@class='search-pagination']//a[text()='>|']"));
            String href = lastPageLink.getAttribute("href");
            assert href != null;
            String pageValue = href.substring(href.lastIndexOf("page=") + 5);
            return Integer.parseInt(pageValue);
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    protected void accessPage(int index) {
        String letter = LETTERS[index];
        String baseUrl = "https://www.drewnapier.com/Our-Lawyers?name=" + letter + "&practice=";

        this.driver.get(baseUrl);
        MyDriver.waitForPageToLoad();
        int totalPages = getLastPageNumber();

        for (int p = 1; p <= totalPages; p++) {
            if (p > 1) {
                String pageUrl = baseUrl + "&page=" + p;
                this.driver.get(pageUrl);
                MyDriver.waitForPageToLoad();
            }

            getLawyersInPage();
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"head", "director", "chief", "counsel"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("lawyer-item")
                    )
            );
            return siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles) ;
        } catch (Exception e) {
            System.out.println("No lawyers found for the current letter.");
            return List.of();
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3 a")};
        String href = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        return href.startsWith("http") ? href : "https://www.drewnapier.com/" + href;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h3 a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("lawyer-item__text--practice"),
                By.cssSelector("ul > li")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("p.contact a"));
            return super.getSocials(socials, false);
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
                "country", "Singapore",
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "6565350733" : socials[1]
        );
    }
}
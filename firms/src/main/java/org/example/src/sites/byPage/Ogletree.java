package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Ogletree extends ByPage {
    private final By[] byRoleArray = {
            By.className("title")
    };

    private String currentCountry = "";


    public Ogletree() {
        super(
            "Ogletree",
            "",
            8,
            3
        );
    }

    private final String[] otherLinks = {
            "https://ogletree.com/people/?keyword=&location=220&title=&practice=&bar=&_gl=1*h42n6b*_up*MQ..*_ga*ODk5MDUxNTk4LjE3NjA3MzU3NjY.*_ga_V4WT9JNBFT*czE3NjA3MzU3NjUkbzEkZzAkdDE3NjA3MzU3NjUkajYwJGwwJGgw",
            "https://ogletree.com/people/?keyword=&location=71649&title=&practice=&bar=&_gl=1*uqdf9z*_up*MQ..*_ga*ODk5MDUxNTk4LjE3NjA3MzU3NjY.*_ga_V4WT9JNBFT*czE3NjA3MzU3NjUkbzEkZzEkdDE3NjA3MzY2NzgkajYwJGwwJGgw",
            "https://ogletree.com/people/?keyword=&location=256&title=&practice=&bar=&_gl=1*ajkxby*_up*MQ..*_ga*ODk5MDUxNTk4LjE3NjA3MzU3NjY.*_ga_V4WT9JNBFT*czE3NjA3MzU3NjUkbzEkZzEkdDE3NjA3MzY5MDYkajYwJGwwJGgw",
            "https://ogletree.com/people/?keyword=&location=262&title=&practice=&bar=&_gl=1*1ax6h97*_up*MQ..*_ga*ODk5MDUxNTk4LjE3NjA3MzU3NjY.*_ga_V4WT9JNBFT*czE3NjA3MzU3NjUkbzEkZzEkdDE3NjA3MzY5NDEkajI1JGwwJGgw",
            "https://ogletree.com/people/?keyword=&location=282&title=&practice=&bar=&_gl=1*18ckydn*_up*MQ..*_ga*ODk5MDUxNTk4LjE3NjA3MzU3NjY.*_ga_V4WT9JNBFT*czE3NjA3MzU3NjUkbzEkZzEkdDE3NjA3MzcwMDckajM5JGwwJGgw",
            "https://ogletree.com/people/?keyword=&location=322&title=&practice=&bar=&_gl=1*1hva3ve*_up*MQ..*_ga*ODk5MDUxNTk4LjE3NjA3MzU3NjY.*_ga_V4WT9JNBFT*czE3NjA3MzU3NjUkbzEkZzEkdDE3NjA3MzcwNDMkajMkbDAkaDA.",
            "https://ogletree.com/people/?keyword=&location=66756&title=&practice=&bar=&_gl=1*1hkj0q6*_up*MQ..*_ga*ODk5MDUxNTk4LjE3NjA3MzU3NjY.*_ga_V4WT9JNBFT*czE3NjA3MzU3NjUkbzEkZzEkdDE3NjA3MzY5ODYkajYwJGwwJGgw",
            "https://ogletree.com/people/?keyword=&location=331&title=&practice=&bar=&_gl=1*17gb568*_up*MQ..*_ga*ODk5MDUxNTk4LjE3NjA3MzU3NjY.*_ga_V4WT9JNBFT*czE3NjA3MzU3NjUkbzEkZzEkdDE3NjA3MzY5NjQkajIkbDAkaDA.",
    };

    private String setIndexAndCountry(int index) {
        switch (index) {
            case 0, 6:
                currentCountry = "Germany";
                break;
            case 1, 5, 7:
                currentCountry = "Canada";
                break;
            case 2:
                currentCountry = "England";
                break;
            case 3:
                currentCountry = "Mexico";
                break;
            case 4:
                currentCountry = "France";
                break;
            default:
                currentCountry = "Unknown";
                break;
        }

        return otherLinks[index];
    }




    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(setIndexAndCountry(index));
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("article.flex.flex-col")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h3 > a[href*='https://ogletree.com/people/']")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }


    private String getName(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h3 > a[href*='https://ogletree.com/people/']")
        };
        return extractor.extractLawyerText(container, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement container) throws LawyerExceptions {
        return extractor.extractLawyerText(container, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }


    private String[] getSocials(WebElement lawyer, String name) {
        String name_ = TreatLawyerParams.treatName(name);
        String[] split = TreatLawyerParams.treatEmail(name_)
                .split(" ");

        String email = split[0] + "."  + split[split.length - 1] + "@ogletree.com";
        String phone = "";

        try {
            List<WebElement> socials = lawyer
                    .findElements(By.cssSelector("div"));
            String[] socials1 = super.getSocials(socials, true);
            socials1[0] = email;
            return socials1;

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);

        String[] socials = this.getSocials(lawyer, name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", currentCountry,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
package org.example.src.sites.asia;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Mourant extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "1284", "the British Virgin Islands",
            "1345", "the Cayman Islands",
            "44", "Guernsey, Jersey, London",
            "65", "Singapore",
            "230", "Mauritius",
            "352", "Luxembourg",
            "852", "Hong Kong"
    );

    private final By[] byRoleArray = {
            By.className("wp-component-card-contact__job-title")
    };

    private final VCard vCard = VCard.withDefaultPatterns();

    public Mourant() {
        super(
            "Mourant",
            "https://www.mourant.com/people/",
            11,
            2
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnAddBtn(By.id("cookiescript_accept"));
        MyDriver.clickOnElementMultipleTimes(By.xpath("//*[@id=\"main\"]/section[2]/div/div/div/div/div[4]/button"), 10, 1);
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("wp-component-card-contact__content")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String href = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[href*='https://www.mourant.com/people/']")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(href);
        return href;
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h1")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("h2")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }


    private String[] getSocials() {
        try {
            WebElement vcardLink = driver.findElement(By.cssSelector("a[href$='.vcf']"));
            String href = vcardLink.getAttribute("href");
            return vCard.getSocials(href);
        } catch (Exception e) {
            System.err.println("Mourant: error fetching vCard — " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("wp-component-person-connect__link-blocks"));
        WebElement header = driver.findElement(By.className("wp-block-hero-block__heading-group"));

        String[] socials = this.getSocials();

        return Map.of(
                "link", link,
                "name", this.getName(header),
                "role", this.getRole(header),
                "firm", this.name,
                "country", siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, socials[1], socials[1]),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}

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
import java.util.Objects;

public class Curtis extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            Map.entry("astana", "Kazakhstan"),
            Map.entry("beijing", "China"),
            Map.entry("bogotá", "Colombia"),
            Map.entry("brussels", "Belgium"),
            Map.entry("buenos aires", "Argentina"),
            Map.entry("dubai", "the UAE"),
            Map.entry("frankfurt", "Germany"),
            Map.entry("geneva", "Switzerland"),
            Map.entry("london", "England"),
            Map.entry("mexico city", "Mexico"),
            Map.entry("milan", "Italy"),
            Map.entry("muscat", "Oman"),
            Map.entry("paris", "France"),
            Map.entry("riyadh", "Saudi Arabia"),
            Map.entry("rome", "Italy")
    );

    private String currentRole = "";

    public Curtis() {
        super(
                "Curtis",
                "https://www.curtis.com/our-people?menu%5BattorneyTitle%5D=Partner",
                2,
                3
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.curtis.com/our-people?menu%5BattorneyTitle%5D=Counsel";
        String url = index == 0 ? this.link : otherUrl;
        currentRole = index == 0 ? "Partner" : "Counsel";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1500L);
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("li.ais-InfiniteHits-item")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.card-module__row-block-cont-hdr-link--team a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("bio-hero__block-info-cont-hdr")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".bio-hero__block-info-cont-sub--lite a")};
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "USA");
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.className("bio-hero__block-info-cont-icon--email")).getAttribute("href");
            phone = lawyer.findElement(By.cssSelector("p.bio-hero__block-info-cont-txt > a[href^='tel:']")).getAttribute("href");
        } catch (Exception e) {}
        return new String[]{email, phone};
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("bio-hero__block-info"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", currentRole,
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
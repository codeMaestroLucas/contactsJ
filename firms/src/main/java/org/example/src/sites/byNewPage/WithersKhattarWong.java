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

import static java.util.Map.entry;

public class WithersKhattarWong extends ByNewPage {

    public WithersKhattarWong() {
        super(
                "Withers KhattarWong",
                "https://www.withersworldwide.com/en-gb/people",
                32,
                3
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("british virgin islands", "the British Virgin Islands"),
            entry("geneva", "Switzerland"),
            entry("hong kong", "China"),
            entry("london", "England"),
            entry("milan", "Italy"),
            entry("padua", "Italy"),
            entry("singapore", "Singapore"),
            entry("tokyo", "Japan")
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.withersworldwide.com/en-gb/people?page=" + (index) + "&ses=1#filter";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("personList__cardWrapper")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".flip-card-front p")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("personList__cardLink")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("hero__content"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String phone = extractor.extractLawyerAttribute(container, new By[]{By.className("phoneNumber__number")}, "PHONE", "textContent", LawyerExceptions::phoneException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(container, new By[]{By.className("title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(container, new By[]{By.className("details")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", this.getCountry(container),
                "practice_area", "",
                "email", socials[0],
                "phone", phone.isEmpty() ? "6565353112" : phone
        );
    }

    private String getCountry(WebElement container) {
        String country = container.findElement(By.cssSelector("p.details > a")).getAttribute("textContent");
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, country);
    }
}

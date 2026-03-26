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

public class KRySGlobal extends ByPage {

    public KRySGlobal() {
        super(
                "KRyS Global",
                "https://www.krys-global.com/team/",
                1
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "cayman islands", "the Cayman Islands",
            "bvi/east caribbean", "the BVI",
            "bermuda", "Bermuda",
            "guernsey", "Guernsey"
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

        MyDriver.clickOnElement(By.xpath("//*[@id=\"post-111\"]/div/div[2]/div[2]/div[6]/div[2]/a"));
        Thread.sleep(2000);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("talent-list-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("talent-list-item-position")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String country = extractor.extractLawyerText(lawyer, new By[]{By.className("talent_list_item_location")}, "COUNTRY", LawyerExceptions::countryException);
        country = siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY ,country, "USA");

        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("open_talent_detail")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("talent-list-name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("talent-list-item-position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "13459474700" : socials[1]
        );
    }
}

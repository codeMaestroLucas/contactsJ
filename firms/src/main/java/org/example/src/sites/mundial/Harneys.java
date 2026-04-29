package org.example.src.sites.mundial;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Harneys extends ByPage {

    public Harneys() {
        super(
                "HARNEYS",
                "https://www.harneys.com/people/?filters=1227%2C1229&sort=5",
                23,
                4
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("bermuda", "Bermuda"),
            entry("british virgin islands", "the British Virgin Islands"),
            entry("cayman islands", "the Cayman Islands"),
            entry("cyprus", "Cyprus"),
            entry("dubai", "the UAE"),
            entry("hong kong", "China"),
            entry("jersey", "Jersey"),
            entry("london", "England"),
            entry("luxembourg", "Luxembourg"),
            entry("shanghai", "China"),
            entry("singapore", "Singapore")
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.harneys.com/people/?filters=1227%2C1229&sort=5&page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("profile-card"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("profile-card-content-info__title")}, true);
    }
    
    private String getCountry(WebElement lawyer) {
        String country = lawyer.getText();
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "England");
    }
    

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("profile-card-content-info__name")}, "NAME", LawyerExceptions::nameException);
        String[] socials = this.getSocialsFromText(lawyer.getText());
        String country = this.getCountry(lawyer);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("profile-card-link")}, "LINK", "href", LawyerExceptions::linkException),
                "name", name,
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("profile-card-content-info__title")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "442037523600" : socials[1]
        );
    }
}

package org.example.src.sites.to_test.africa;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Bowmans extends ByPage {

    public Bowmans() {
        super(
                "Bowmans",
                "https://bowmanslaw.com/our-people/",
                46
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "kenya", "Kenya",
            "mauritius", "Mauritius",
            "nambia", "Namibia",
            "tanzania", "Tanzania",
            "zambia", "Zambia"
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        } else {
            MyDriver.clickOnElement(By.cssSelector("ul.pagination > li.next-prev > a[aria-label=\"Next\"]"));
            Thread.sleep(1500);
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.lawyer"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("position")}, true);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[] {By.className("address_row")}, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "South Africa");
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocialsFromText(lawyer.findElement(By.className("lawyer-contact")).getText());

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "27116699300" : socials[1]
        );
    }
}

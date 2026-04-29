package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BrigardUrrutia extends ByNewPage {

    public BrigardUrrutia() {
        super(
                "Brigard Urrutia",
                "https://www.bu.com.co/en/lawyers",
                1,
                2
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "london", "England",
            "singapore", "Singapore"
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElementMultipleTimes(
                By.xpath("/html/body/div[1]/div[2]/div[2]/section/div/div[2]/div/ul/li/a"),
                10, 0.8
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("article.lawyer-teaser-container"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("field--name-field-membership")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h3.name a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }
    
    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[] {By.cssSelector("div.field.field--name-field-admitido-en.field--type-string.field--label-above")}, "COUNTRY", LawyerExceptions::countryException);
        country = country.toLowerCase().replace("admitted to practice law in", "");
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Colombia");
    }
    

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h3.name span")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("lawyer-card-info"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("p-job-title")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerText(container, new By[]{By.className("field--name-field-email")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(container, new By[]{By.className("p-tel")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", this.getCountry(container),
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "576013462011" : phone
        );
    }
}

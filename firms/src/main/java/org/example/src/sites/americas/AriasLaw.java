package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class AriasLaw extends ByPage {

    public AriasLaw() {
        super(
                "ARIAS Law",
                "https://www.ariaslaw.com/en/our-people/0",
                11
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "gt", "Guatemala",
            "sv", "El Salvador",
            "hn", "Honduras",
            "nc", "Nicaragua",
            "cr", "Costa Rica",
            "pn", "Panama"
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        } else {
            MyDriver.clickOnElement(By.xpath("//*[@id=\"inspire\"]/div[1]/main/div/div[3]/div[2]/div/div/div[13]/nav/ul/li[9]/button"));
            Thread.sleep(2000);
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.team-presentation"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("designation")}, true);
    }
    
    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[] {By.className("designation")}, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "");
    }
    
    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocialsFromText(lawyer.getText());

        return Map.of(
                "link", this.link,
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("designation")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "50322570900" : socials[1]
        );
    }
}

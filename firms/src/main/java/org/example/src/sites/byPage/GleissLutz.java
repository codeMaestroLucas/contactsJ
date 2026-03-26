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

public class GleissLutz extends ByPage {

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "brussels", "Belgium",
            "london", "England"
    );

    public GleissLutz() {
        super(
                "Gleiss Lutz",
                "https://www.gleisslutz.com/en/experts",
                28
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
        } else  {
            MyDriver.clickOnElement(By.xpath("//*[@id=\"paragraph-11199\"]/div/div/div[2]/div/nav/ul/li[4]/a"));
            Thread.sleep(1000);
        }
        MyDriver.waitForPageToLoad();

    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("teaser")));
        } catch (Exception e) {
            return List.of();
        }
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By [] byArray = {
                By.cssSelector("a[href*='/en/offices/']")
        };
        String country = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "textContent", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Germany");
    }


    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector(".contact a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("more")}, "LINK", "href", LawyerExceptions::linkException),
                "name", this.getName(lawyer),
                "role", "----",
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("competencies")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException).replace("Expertise", ""),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "4969955140" : socials[1]
        );
    }

    private Object getName(WebElement lawyer) throws LawyerExceptions {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("name")}, "NAME", "textContent", LawyerExceptions::nameException);
        name = name.replaceAll("\\s+T\\s+.*$", "").trim();
        String[] parts = name.split(",");
        return parts[parts.length - 1].trim() + " " + parts[0].trim();
    }
}

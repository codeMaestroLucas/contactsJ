package org.example.src.sites.to_test;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class ADVANTBeiten extends ByNewPage {

    public ADVANTBeiten() {
        super(
                "ADVANT Beiten",
                "https://www.advant-beiten.com/en/experts",
                1,
                2
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("beijing", "China"),
            entry("brussels", "Belgium"),
            entry("london", "England"),
            entry("moscow", "Russia")
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        // More than 30 rolls
        MyDriver.rollDown(10, 1.5);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("a.card.team"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".text:last-child")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[] {By.className("location")}, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Germany");
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".text:last-child")}, "ROLE", LawyerExceptions::roleException);
        String country = this.getCountry(lawyer);

        String link = this.openNewTab(lawyer);

        String container = driver.findElement(By.className("header-block-cv-two-col-flex")).getText();
        String[] socials = super.getSocialsFromText(container);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", container,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "49697560950" : socials[1]
        );
    }
}

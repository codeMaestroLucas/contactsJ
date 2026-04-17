package org.example.src.sites.mundial;

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

public class Luther extends ByNewPage {

    public Luther() {
        super(
                "Luther",
                "https://www.luther-lawfirm.com/en/team",
                68,
                2
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("bangkok", "Thailand"),
            entry("brussels", "Belgium"),
            entry("delhi-gurugram", "India"),
            entry("ho chi minh city", "Vietnam"),
            entry("jakarta", "Indonesia"),
            entry("kuala lumpur", "Malaysia"),
            entry("london", "England"),
            entry("luxembourg", "Luxembourg"),
            entry("shanghai", "China"),
            entry("singapore", "Singapore"),
            entry("yangon", "Myanmar")
    );

    @Override
    protected void accessPage(int index) {
        String otherUrl = "https://www.luther-lawfirm.com/en/team?tx_fwluther_listteamcontactnew%5BcurrentPage%5D=" + (index + 1) + "&tx_fwluther_listteamcontactnew%5Bfilter%5D%5BdetailPage%5D=2331&tx_fwluther_listteamcontactnew%5Bfilter%5D%5Blocation%5D=&tx_fwluther_listteamcontactnew%5Bfilter%5D%5Bname%5D=&tx_fwluther_listteamcontactnew%5Bfilter%5D%5Bpid%5D=2303&tx_fwluther_listteamcontactnew%5Bfilter%5D%5Bposition%5D=&tx_fwluther_listteamcontactnew%5Bfilter%5D%5Bquali%5D=&tx_fwluther_listteamcontactnew%5Bfilter%5D%5Bservice%5D=&tx_fwluther_listteamcontactnew%5Bfilter%5D%5BstoragePids%5D=5&cHash=f6f1b45875e02004acb0eeefc93c1c78";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("title")}, false);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("team-item-name")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement subPage) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("span[itemprop='position']")};
        return extractor.extractLawyerText(subPage, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement subPage) {
        try {
            List<WebElement> socials = subPage.findElements(By.tagName("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String country = this.getCountry(lawyer);
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("team-top"));
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", this.getRole(container),
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("contact-info")};
        String country = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Germany");
    }
}

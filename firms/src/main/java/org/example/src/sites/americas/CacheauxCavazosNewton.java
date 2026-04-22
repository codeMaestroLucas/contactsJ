package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class CacheauxCavazosNewton extends ByNewPage {

    public CacheauxCavazosNewton() {
        super(
                "Cacheaux, Cavazos & Newton",
                "https://ccn-law.com/en/our-team/",
                6
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("san antonio", "USA"),
            entry("austin", "USA"),
            entry("mcallen", "USA")
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
            MyDriver.clickOnAddBtn(By.cssSelector("button.cky-btn.cky-btn-accept"));
        } else {
            MyDriver.rollDownToBottom(0.4);
            MyDriver.clickOnElement(By.xpath("/html/body/div[5]/section[2]/div/div[2]/div/div[3]/div/div/div/div[7]"));
            Thread.sleep(2500);
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.elementor-element-1f96e94"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("h5")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getCountry(String country) throws LawyerExceptions {
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Mexico");
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h4")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h5")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div[5]/section[1]/div[2]/div[2]/div"));
        String[] socials = super.getSocialsFromText(container.getText());
        String country = this.getCountry(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", extractor.extractLawyerAttribute(container, new By[]{By.className("jet-listing-dynamic-terms")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "524422620316" : socials[1]
        );
    }
}

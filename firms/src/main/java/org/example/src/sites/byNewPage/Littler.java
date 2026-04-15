package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Littler extends ByNewPage {

    private final By[] byRoleArray = {
            By.cssSelector(".author__content span")
    };

    public Littler() {
        super(
                "Littler",
                "https://www.littler.com/people",
                25,
                3
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("351", "Portugal"),
            entry("31", "the Netherlands"),
            entry("34", "Spain"),
            entry("57", "Colombia"),
            entry("32", "Belgium"),
            entry("49", "Germany"),
            entry("45", "Denmark"),
            entry("353", "Ireland"),
            entry("44", "England"),
            entry("502", "Guatemala"),
            entry("505", "Nicaragua"),
            entry("52", "Mexico"),
            entry("39", "Italy"),
            entry("47", "Norway"),
            entry("507", "Panama"),
            entry("33", "France"),
            entry("593", "Ecuador"),
            entry("506", "Costa Rica"),
            entry("1", "Canada"),
            entry("1-787", "Puerto Rico"),
            entry("504", "Honduras"),
            entry("503", "El Salvador"),
            entry("1-809", "the Dominican Republic"),
            entry("55", "Brazil"),
            entry("65", "Singapore"),
            entry("43", "Austria"),
            entry("41", "Switzerland")
    );

    int[] indexes = {
            1, 2, 3, 5, 6, 7, 8, 9, 10, 11,
            12, 13, 14, 15, 16, 17, 18, 19,
            20, 21, 22, 23, 24, 25, 26,
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        }

        String xpath = "//*[@id=\"littler-professional-search\"]/div/div[2]/div/div[1]/div/div/div/button[" + indexes[index] + "]";
        MyDriver.clickOnElement(By.xpath(xpath));
        Thread.sleep(500);
    }



    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.author-grid__content > div.author"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("author__content")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("author__name")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("author__name")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("hero__content"));
        String email = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href^='mailto:']")}, "EMAIL", "href", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("li[role='option'] a")}, "PHONE", "textContent", LawyerExceptions::phoneException);
        String country = siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, phone, "USA");

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "5713174628" : phone
        );
    }
}

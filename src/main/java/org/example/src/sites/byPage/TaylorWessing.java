package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;

public class TaylorWessing extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("asia", "Asia"),
            entry("austria", "Austria"),
            entry("belgium", "Belgium"),
            entry("cee/see", "CEE/SEE"),
            entry("china", "China"),
            entry("czech republic", "the Czech Republic"),
            entry("france", "France"),
            entry("germany", "Germany"),
            entry("hamburg", "Germany"),
            entry("hong kong sar", "Hong Kong"),
            entry("hungary", "Hungary"),
            entry("ireland", "Ireland"),
            entry("middle east", "Middle East"),
            entry("netherlands", "the Netherlands"),
            entry("poland", "Poland"),
            entry("slovakia", "Slovakia"),
            entry("south korea", "Korea (South)"),
            entry("uae", "the UAE"),
            entry("uk", "England"),
            entry("ukraine", "Ukraine"),
            entry("usa", "EUA"),
            entry("united kingdom", "England"),
            entry("united states", "USA"),
            entry("the netherlands", "the Netherlands"),
            entry("united arab emirates", "the UAE"),
            entry("hong kong", "Hong Kong")
    );


    private final By[] byRoleArray = {
            By.className("team-members__item--title")
    };


    public TaylorWessing() {
        super(
            "Taylor Wessing",
            "https://www.taylorwessing.com/en/people",
            60,
            3
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.taylorwessing.com/en/people?page=" + (index + 1) + "#people-listing";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        this.siteUtl.clickOnAddBtn(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("team-members__item--content")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("title");
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getCountry(String linkToLawyer) {
        linkToLawyer = linkToLawyer
                .replace("https://www.taylorwessing.com/en/people/", "")
                .split("/")[0];
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, linkToLawyer);
    }


    private String[] getSocials(WebElement lawyer) {
        String email = "";
        try {
            String phone = lawyer.findElement(By.className("telephone"))
                    .findElement(By.className("person__description-contact-item-link"))
                    .getAttribute("href");

            String onclickEmailValue = lawyer.findElement(By.className("email"))
                    .findElement(By.className("email-link"))
                    .getAttribute("onclick");

            Pattern pattern = Pattern.compile("'mailto:' \\+ '([^']+)' \\+ '@' \\+ '([^']+)'");
            Matcher matcher = pattern.matcher(onclickEmailValue);

            if (matcher.find()) {
                email = matcher.group(1) + "@" + matcher.group(2);
            }

            return new String[]{ email, phone};

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        String myLink = this.getLink(lawyer);

        return Map.of(
            "link", myLink,
            "name", this.getName(lawyer),
            "role", this.getRole(lawyer),
            "firm", this.name,
            "country", this.getCountry(myLink),
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1]);
    }
}

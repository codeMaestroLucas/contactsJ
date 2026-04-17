package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

public class EvershedsSutherland extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("luanda", "Angola"),
            entry("hong kong sar", "China"),
            entry("shanghai", "China"),
            entry("vienna", "Austria"),
            entry("brussels", "Belgium"),
            entry("sofia", "Bulgaria"),
            entry("prague", "the Czech Republic"),
            entry("tallinn", "Estonia"),
            entry("helsinki", "Finland"),
            entry("jyväskylä", "Finland"),
            entry("oulu", "Finland"),
            entry("tampere", "Finland"),
            entry("turku", "Finland"),
            entry("paris", "France"),
            entry("dusseldorf", "Germany"),
            entry("frankfurt", "Germany"),
            entry("hamburg", "Germany"),
            entry("munich", "Germany"),
            entry("budapest", "Hungary"),
            entry("baghdad", "Iraq"),
            entry("erbil", "Iraq"),
            entry("dublin", "Ireland"),
            entry("milan", "Italy"),
            entry("rome", "Italy"),
            entry("amman", "Jordan"),
            entry("riga", "Latvia"),
            entry("vilnius", "Lithuania"),
            entry("luxembourg", "Luxembourg"),
            entry("port louis", "Mauritius"),
            entry("maputo", "Mozambique"),
            entry("amsterdam", "the Netherlands"),
            entry("rotterdam", "the Netherlands"),
            entry("warsaw", "Poland"),
            entry("faro", "Portugal"),
            entry("lisbon", "Portugal"),
            entry("porto", "Portugal"),
            entry("doha", "Qatar"),
            entry("bucharest", "Romania"),
            entry("riyadh", "Saudi Arabia"),
            entry("bratislava", "Slovakia"),
            entry("johannesburg", "South Africa"),
            entry("madrid", "Spain"),
            entry("stockholm", "Sweden"),
            entry("berne", "Switzerland"),
            entry("geneva", "Switzerland"),
            entry("zug", "Switzerland"),
            entry("zurich", "Switzerland"),
            entry("tunis", "Tunisia"),
            entry("abu dhabi", "the UAE"),
            entry("dubai", "the UAE"),
            entry("atlanta", "USA"),
            entry("austin", "USA"),
            entry("chicago", "USA"),
            entry("houston", "USA"),
            entry("new york", "USA"),
            entry("silicon valley", "USA"),
            entry("sacramento ", "USA"),
            entry("san diego", "USA"),
            entry("san francisco", "USA"),
            entry("washington, dc", "USA")
    );

    public EvershedsSutherland() {
        super(
                "Eversheds Sutherland",
                "https://www.eversheds-sutherland.com/en/lithuania/people?role=Executive+Partner%7CManaging+Partner%7CPartner%7CChair%7CLegal+Director%7CSenior+Partner%7CAdviser%7CCounsel%7CGlobal+Co-CEO%7COf+Counsel%7CPrincipal+Associate%7CPrincipal+Solicitor%7CPrincipal+Legal+Manager%7CSenior+Associate%7CSenior+Counsel%7CSpecial+Counsel&office=Angola%7CAsia%7CAustria%7CBelgium%7CBulgaria%7CCzech+Republic%7CEstonia%7CUnited+Kingdom%7CUnited+Arab+Emirates%7CTunisia%7CSwitzerland%7CSweden%7CSpain%7CSouth+Africa%7CSlovakia%7CSaudi+Arabia%7CRomania%7CQatar%7CPortugal%7CPoland%7CNetherlands%7CMozambique%7CMauritius%7CLuxembourg%7CLithuania%7CLatvia%7CJordan%7CItaly%7CIreland%7CIraq%7CHungary%7CGermany%7CFrance%7CFinland",
                95,
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.eversheds-sutherland.com/en/lithuania/people?role=Executive+Partner%7CManaging+Partner%7CPartner%7CChair%7CLegal+Director%7CSenior+Partner%7CAdviser%7CCounsel%7CGlobal+Co-CEO%7COf+Counsel%7CPrincipal+Associate%7CPrincipal+Solicitor%7CPrincipal+Legal+Manager%7CSenior+Associate%7CSenior+Counsel%7CSpecial+Counsel&office=Angola%7CAsia%7CAustria%7CBelgium%7CBulgaria%7CCzech+Republic%7CEstonia%7CUnited+Kingdom%7CUnited+Arab+Emirates%7CTunisia%7CSwitzerland%7CSweden%7CSpain%7CSouth+Africa%7CSlovakia%7CSaudi+Arabia%7CRomania%7CQatar%7CPortugal%7CPoland%7CNetherlands%7CMozambique%7CMauritius%7CLuxembourg%7CLithuania%7CLatvia%7CJordan%7CItaly%7CIreland%7CIraq%7CHungary%7CGermany%7CFrance%7CFinland&page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("card")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("card__role")}, false);
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


    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("card__name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("card__role")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.cssSelector("div.hero-people__text.gutter"));

        String email = extractor.extractLawyerAttribute(container, new By[]{By.className("hero-people__email-link")}, "EMAIL", "href", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerAttribute(container, new By[]{By.className("hero-people__phone-link")}, "PHONE", "href", LawyerExceptions::phoneException);


        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", this.getCountry(container),
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "34914294333" : phone
        );
    }

    private @Nullable String getCountry(WebElement container) throws LawyerExceptions {
        String country = extractor.extractLawyerAttribute(container, new By[]{By.className("hero-people__location-link")}, "COUNTRY", "textContent", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "England");
    }
}

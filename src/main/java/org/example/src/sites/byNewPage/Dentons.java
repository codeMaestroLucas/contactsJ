package org.example.src.sites.byNewPage;

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

import static java.util.Map.entry;

public class Dentons extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("abu dhabi", "the UAE"), entry("abuja", "Nigeria"), entry("adelaide", "Australia"),
            entry("almaty", "Kazakhstan"), entry("amman", "Jordan"), entry("amsterdam", "the Netherlands"),
            entry("anguilla", "Anguilla"), entry("antigua and barbuda", "Antigua and Barbuda"),
            entry("argentina", "Argentina"), entry("astana", "Kazakhstan"), entry("auckland", "New Zealand"),
            entry("australia", "Australia"), entry("azerbaijan", "Azerbaijan"), entry("baku", "Azerbaijan"),
            entry("barbados", "Barbados"), entry("beijing", "China"), entry("beirut", "Lebanon"),
            entry("belgium", "Belgium"), entry("belo horizonte", "Brazil"), entry("bengaluru", "India"),
            entry("berlin", "Germany"), entry("bogota", "Colombia"), entry("bolivia", "Bolivia"),
            entry("brasilia", "Brazil"), entry("bratislava", "Slovak Republic"), entry("brazil", "Brazil"),
            entry("brisbane", "Australia"), entry("british virgin islands", "the British Virgin Islands"),
            entry("brussels", "Belgium"), entry("bucharest", "Romania"), entry("budapest", "Hungary"),
            entry("buenos aires", "Argentina"), entry("cairo", "Egypt"), entry("calgary", "Canada"),
            entry("canada", "Canada"), entry("cape town", "South Africa"), entry("caracas", "Venezuela"),
            entry("casablanca", "Morocco"), entry("cayman islands", "the Cayman Islands"), entry("chennai", "India"),
            entry("chile", "Chile"), entry("china", "China"), entry("christchurch", "New Zealand"),
            entry("colombia", "Colombia"), entry("costa rica", "Costa Rica"), entry("czech republic", "the Czech Republic"),
            entry("dar es salaam", "Tanzania"), entry("doha", "Qatar"), entry("dominica", "Dominica"),
            entry("dubai", "the UAE"), entry("dublin", "Ireland"), entry("dusseldorf", "Germany"),
            entry("ebene", "Mauritius"), entry("ecuador", "Ecuador"), entry("edinburgh", "England"),
            entry("edmonton", "Canada"), entry("egypt", "Egypt"), entry("el salvador", "El Salvador"),
            entry("france", "France"), entry("frankfurt", "Germany"), entry("georgia", "Georgia"),
            entry("germany", "Germany"), entry("gift city", "India"), entry("glasgow", "England"),
            entry("grand bay", "Mauritius"), entry("grenada", "Grenada"), entry("guatemala", "Guatemala"),
            entry("guatemala city", "Guatemala"), entry("guyana", "Guyana"), entry("hanoi", "Vietnam"),
            entry("ho chi minh city", "Vietnam"), entry("honduras", "Honduras"), entry("hong kong", "Hong Kong"),
            entry("hungary", "Hungary"), entry("hyderabad", "India"), entry("india", "India"),
            entry("indonesia", "Indonesia"), entry("ireland", "Ireland"), entry("istanbul", "Türkiye"),
            entry("italy", "Italy"), entry("jakarta", "Indonesia"), entry("jamaica", "Jamaica"),
            entry("jeddah", "Saudi Arabia"), entry("johannesburg", "South Africa"), entry("jordan", "Jordan"),
            entry("kampala", "Uganda"), entry("kazakhstan", "Kazakhstan"), entry("kenya", "Kenya"),
            entry("kuala lumpur", "Malaysia"), entry("kyiv", "Ukraine"), entry("la paz", "Bolivia"),
            entry("lagos", "Nigeria"), entry("lebanon", "Lebanon"), entry("liberia", "Costa Rica"),
            entry("lima", "Peru"), entry("london", "England"), entry("lusaka", "Zambia"),
            entry("luxembourg", "Luxembourg"), entry("madrid", "Spain"), entry("malaysia", "Malaysia"),
            entry("managua", "Nicaragua"), entry("manila", "the Philippines"), entry("maputo", "Mozambique"),
            entry("mauritius", "Mauritius"), entry("medellin", "Colombia"), entry("melbourne", "Australia"),
            entry("mexico", "Mexico"), entry("mexico city", "Mexico"), entry("milan", "Italy"),
            entry("milton keynes", "England"), entry("mombasa", "Kenya"), entry("monterrey", "Mexico"),
            entry("montevideo", "Uruguay"), entry("montreal", "Canada"), entry("montserrat", "Montserrat"),
            entry("morocco", "Morocco"), entry("mozambique", "Mozambique"), entry("mumbai", "India"),
            entry("munich", "Germany"), entry("muscat", "Oman"), entry("myanmar", "Myanmar"),
            entry("nairobi", "Kenya"), entry("namibia", "Namibia"), entry("netherlands", "the Netherlands"),
            entry("new delhi", "India"), entry("new zealand", "New Zealand"), entry("nicaragua", "Nicaragua"),
            entry("nigeria", "Nigeria"), entry("noida", "India"), entry("oman", "Oman"), entry("ottawa", "Canada"),
            entry("panama", "Panama"), entry("panama city", "Panama"), entry("papua new guinea", "Papua New Guinea"),
            entry("paris", "France"), entry("perth", "Australia"), entry("peru", "Peru"),
            entry("philippines", "the Philippines"), entry("poland", "Poland"), entry("port harcourt", "Nigeria"),
            entry("port louis", "Mauritius"), entry("port moresby", "Papua New Guinea"), entry("prague", "the Czech Republic"),
            entry("qatar", "Qatar"), entry("quito", "Ecuador"), entry("ribeirao preto", "Brazil"),
            entry("rio de janeiro", "Brazil"), entry("riyadh", "Saudi Arabia"), entry("romania", "Romania"),
            entry("rome", "Italy"), entry("russia", "Russia"), entry("san jose", "Costa Rica"),
            entry("san salvador", "El Salvador"), entry("santa cruz", "Bolivia"), entry("santiago", "Chile"),
            entry("sao paulo", "Brazil"), entry("saudi arabia", "Saudi Arabia"), entry("scotland", "England"),
            entry("seoul", "Korea (South)"), entry("singapore", "Singapore"), entry("slovak republic", "Slovak Republic"),
            entry("south africa", "South Africa"), entry("south korea", "Korea (South)"), entry("spain", "Spain"),
            entry("st kitts and nevis", "St. Kitts and Nevis"), entry("st lucia", "St. Lucia"),
            entry("st vincent and the grenadines", "St. Vincent and The Grenadines"), entry("sydney", "Australia"),
            entry("tanzania", "Tanzania"), entry("tashkent", "Uzbekistan"), entry("tbilisi", "Georgia"),
            entry("tegucigalpa", "Honduras"), entry("tokyo", "Japan"), entry("toronto", "Canada"),
            entry("trinidad and tobago", "Trinidad and Tobago"), entry("tunis", "Tunisia"), entry("tunisia", "Tunisia"),
            entry("turkiye", "Türkiye"), entry("uganda", "Uganda"), entry("ukraine", "Ukraine"),
            entry("united arab emirates", "the UAE"), entry("united kingdom", "England"), entry("uruguay", "Uruguay"),
            entry("uzbekistan", "Uzbekistan"), entry("vancouver", "Canada"), entry("venezuela", "Venezuela"),
            entry("vietnam", "Vietnam"), entry("warsaw", "Poland"), entry("wellington", "New Zealand"),
            entry("windhoek", "Namibia"), entry("yangon", "Myanmar"), entry("zambia", "Zambia")
    );


    private final By[] byRoleArray = {
            By.className("person-info"),
            By.className("info-position"),
            By.cssSelector("p")
    };


    public Dentons() {
        super(
                "Dentons",
                "https://www.dentons.com/en/our-professionals",
                1,
                4
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.id("onetrust-accept-btn-handler"));

        // More than 10 pages (IDK how many, but a lot)
        MyDriver.clickOnElementMultipleTimes(By.className("loadmore-professionals"), 10, 4);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "associate counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("col-desc")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        try {
            By[] byArray = {By.cssSelector("a[id^='name_']")};
            String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
            MyDriver.openNewTab(link);
        } catch (LawyerExceptions e) {
            System.err.println("Failed to open new tab: " + e.getMessage());
        }
    }

    public String getLink() {
        return driver.getCurrentUrl();
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("bio_name_mobile"), By.id("mobiletop")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "outerHTML", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("bio_name_mobile"), By.cssSelector("small")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "outerHTML", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("mobile"), By.className("bio-contact"), By.cssSelector("span")};
        String office = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "outerHTML", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "USA");
    }


    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            phone = lawyer
                    .findElement(By.className("mobile"))
                    .findElement(By.className("callToDevice"))
                    .getAttribute("href");

            email = lawyer
                    .findElement(By.className("social_mobile"))
                    .findElement(By.cssSelector("a[rel^='mailto:']"))
                    .getAttribute("rel");
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{email, phone};
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.id("About"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", this.getLink(),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
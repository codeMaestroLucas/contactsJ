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

import static java.util.Map.entry;

public class CorporateINTL extends ByPage {

    public CorporateINTL() {
        super(
                "Corporate INTL",
                "https://www.corp-intl.com/directory/",
                1,
                5
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("afghanistan", "Afghanistan"),
            entry("albania", "Albania"),
            entry("algeria", "Algeria"),
            entry("andorra", "Andorra"),
            entry("angola", "Angola"),
            entry("anguilla", "Anguilla"),
            entry("antigua and barbuda", "Antigua and Barbuda"),
            entry("argentina", "Argentina"),
            entry("armenia", "Armenia"),
            entry("aruba", "Aruba"),
            entry("australia", "Australia"),
            entry("austria", "Austria"),
            entry("azerbaijan", "Azerbaijan"),
            entry("bahamas", "Bahamas"),
            entry("bahrain", "Bahrain"),
            entry("bangladesh", "Bangladesh"),
            entry("barbados", "Barbados"),
            entry("belarus", "Belarus"),
            entry("belgium", "Belgium"),
            entry("belize", "Belize"),
            entry("benin", "Benin"),
            entry("bermuda", "Bermuda"),
            entry("bhutan", "Bhutan"),
            entry("bolivia", "Bolivia"),
            entry("bosnia and herzegovina", "Bosnia and Herzegovina"),
            entry("botswana", "Botswana"),
            entry("brazil", "Brazil"),
            entry("british virgin islands", "the British Virgin Islands"),
            entry("brunei", "Brunei"),
            entry("bulgaria", "Bulgaria"),
            entry("burkina faso", "Burkina Faso"),
            entry("burundi", "Burundi"),
            entry("cambodia", "Cambodia"),
            entry("cameroon", "Cameroon"),
            entry("canada", "Canada"),
            entry("cayman islands", "Cayman Islands"),
            entry("chile", "Chile"),
            entry("china", "China"),
            entry("colombia", "Colombia"),
            entry("congo (dem. rep.)", "Congo (Dem. Rep.)"),
            entry("cook islands", "Cook Islands"),
            entry("costa rica", "Costa Rica"),
            entry("cote d'ivoire", "Cote d'Ivoire"),
            entry("croatia", "Croatia"),
            entry("curaçao", "Curaçao"),
            entry("cyprus", "Cyprus"),
            entry("czech republic", "the Czech Republic"),
            entry("denmark", "Denmark"),
            entry("dominica", "Dominica"),
            entry("dominican republic", "the Dominican Republic"),
            entry("ecuador", "Ecuador"),
            entry("egypt", "Egypt"),
            entry("el salvador", "El Salvador"),
            entry("equatorial guinea", "Equatorial Guinea"),
            entry("eritrea", "Eritrea"),
            entry("estonia", "Estonia"),
            entry("ethiopia", "Ethiopia"),
            entry("faroe islands", "Faroe Islands"),
            entry("fiji islands", "Fiji Islands"),
            entry("finland", "Finland"),
            entry("france", "France"),
            entry("gabon", "Gabon"),
            entry("gambia", "Gambia"),
            entry("georgia", "Georgia"),
            entry("germany", "Germany"),
            entry("ghana", "Ghana"),
            entry("gibraltar", "Gibraltar"),
            entry("greece", "Greece"),
            entry("grenada", "Grenada"),
            entry("guam", "Guam"),
            entry("guatemala", "Guatemala"),
            entry("guernsey", "Guernsey"),
            entry("guinea", "Guinea"),
            entry("haiti", "Haiti"),
            entry("honduras", "Honduras"),
            entry("hong kong", "Hong Kong"),
            entry("hungary", "Hungary"),
            entry("iceland", "Iceland"),
            entry("india", "India"),
            entry("indonesia", "Indonesia"),
            entry("iran", "Iran"),
            entry("iraq", "Iraq"),
            entry("isle of man", "Isle of Man"),
            entry("israel", "Israel"),
            entry("italy", "Italy"),
            entry("jamaica", "Jamaica"),
            entry("japan", "Japan"),
            entry("jersey", "Jersey"),
            entry("jordan", "Jordan"),
            entry("kazakhstan", "Kazakhstan"),
            entry("kenya", "Kenya"),
            entry("korea (south)", "Korea (South)"),
            entry("kosovo", "Kosovo"),
            entry("kuwait", "Kuwait"),
            entry("kyrgyzstan", "Kyrgyzstan"),
            entry("laos", "Laos"),
            entry("latvia", "Latvia"),
            entry("lebanon", "Lebanon"),
            entry("libya", "Libya"),
            entry("liechtenstein", "Liechtenstein"),
            entry("lithuania", "Lithuania"),
            entry("luxembourg", "Luxembourg"),
            entry("macau", "Macau"),
            entry("macedonia", "Macedonia"),
            entry("malaysia", "Malaysia"),
            entry("mali", "Mali"),
            entry("malta", "Malta"),
            entry("mauritania", "Mauritania"),
            entry("mauritius", "Mauritius"),
            entry("mexico", "Mexico"),
            entry("moldova", "Moldova"),
            entry("monaco", "Monaco"),
            entry("mongolia", "Mongolia"),
            entry("morocco", "Morocco"),
            entry("mozambique", "Mozambique"),
            entry("myanmar", "Myanmar"),
            entry("namibia", "Namibia"),
            entry("nepal", "Nepal"),
            entry("netherlands", "the Netherlands"),
            entry("netherlands antilles", "the Netherlands"),
            entry("new zealand", "New Zealand"),
            entry("nicaragua", "Nicaragua"),
            entry("niger", "Niger"),
            entry("nigeria", "Nigeria"),
            entry("northern mariana islands", "Northern Mariana Islands"),
            entry("norway", "Norway"),
            entry("oman", "Oman"),
            entry("pakistan", "Pakistan"),
            entry("panama", "Panama"),
            entry("paraguay", "Paraguay"),
            entry("peru", "Peru"),
            entry("philippines", "the Philippines"),
            entry("poland", "Poland"),
            entry("portugal", "Portugal"),
            entry("puerto rico", "Puerto Rico"),
            entry("qatar", "Qatar"),
            entry("republic of ireland", "Ireland"),
            entry("romania", "Romania"),
            entry("russia", "Russia"),
            entry("rwanda", "Rwanda"),
            entry("saint vincent and the grenadines", "Saint Vincent and The Grenadines"),
            entry("saudi arabia", "Saudi Arabia"),
            entry("senegal", "Senegal"),
            entry("serbia", "Serbia"),
            entry("singapore", "Singapore"),
            entry("slovakia", "Slovakia"),
            entry("slovenia", "Slovenia"),
            entry("south africa", "South Africa"),
            entry("spain", "Spain"),
            entry("sri lanka", "Sri Lanka"),
            entry("sudan", "Sudan"),
            entry("sweden", "Sweden"),
            entry("switzerland", "Switzerland"),
            entry("taiwan", "Taiwan"),
            entry("tanzania", "Tanzania"),
            entry("thailand", "Thailand"),
            entry("togo", "Togo"),
            entry("tunisia", "Tunisia"),
            entry("turkey", "Turkey"),
            entry("turkmenistan", "Turkmenistan"),
            entry("turks and caicos islands", "Turks and Caicos Islands"),
            entry("ukraine", "Ukraine"),
            entry("united arab emirates", "the UAE"),
            entry("united kingdom", "England"),
            entry("united kingdom - england", "England"),
            entry("uruguay", "Uruguay"),
            entry("uzbekistan", "Uzbekistan"),
            entry("venezuela", "Venezuela"),
            entry("vietnam", "Vietnam"),
            entry("yemen", "Yemen"),
            entry("zambia", "Zambia"),
            entry("zimbabwe", "Zimbabwe")
    );

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElement(By.id("ContentPlaceHolder1_ContentPlaceHolder1_searchFirmName"));
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("section.expertresultbox")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find expert result boxes", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("expertresulttitle")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getRawTitle(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("expertresulttitle")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "TITLE", "textContent", LawyerExceptions::nameException);
    }

    private String getName(String rawTitle) {
        // Formato esperado: "Firm Name - Lawyer Name"
        if (rawTitle.contains(" - ")) {
            return rawTitle.split(" - ")[1].trim();
        }
        return rawTitle;
    }

    private String getFirm(String rawTitle) {
        if (rawTitle.contains(" - ")) {
            return rawTitle.split(" - ")[0].trim();
        }
        return this.name;
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("blockcountry"), By.cssSelector("div.col-12:not(.expertresultlabellarge)")};
        String country = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "textContent", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");

    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.xpath(".//div[contains(text(), 'Practice Area:')]/following-sibling::div")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE", "textContent", LawyerExceptions::practiceAreaException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            email = lawyer.findElement(By.xpath(".//div[contains(text(), 'Email:')]/following-sibling::div")).getText().trim();
            phone = lawyer.findElement(By.xpath(".//div[contains(text(), 'Tel:')]/following-sibling::div")).getText().trim();
        } catch (Exception e) {
            System.err.println("Error extracting socials: " + e.getMessage());
        }
        return new String[]{email, phone};
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String rawTitle = this.getRawTitle(lawyer);
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(rawTitle),
                "role", "---",
                "firm", this.getFirm(rawTitle),
                "country", this.getCountry(lawyer),
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
package org.example.src.sites.mundial;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class LeadersIn extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("afghanistan", "Afghanistan"),
            entry("albania", "Albania"),
            entry("algeria", "Algeria"),
            entry("angola", "Angola"),
            entry("anguilla", "Anguilla"),
            entry("argentina", "Argentina"),
            entry("aruba", "Aruba"),
            entry("australia", "Australia"),
            entry("austria", "Austria"),
            entry("azerbaijan", "Azerbaijan"),
            entry("bahamas", "the Bahamas"),
            entry("bahrain", "Bahrain"),
            entry("bangladesh", "Bangladesh"),
            entry("barbados", "Barbados"),
            entry("belgium", "Belgium"),
            entry("belize", "Belize"),
            entry("bermuda", "Bermuda"),
            entry("bolivia", "Bolivia"),
            entry("botswana", "Botswana"),
            entry("brasil", "Brazil"),
            entry("british-virgin-islands", "the British Virgin Islands"),
            entry("bulgaria", "Bulgaria"),
            entry("burkina-faso", "Burkina Faso"),
            entry("burundi", "Burundi"),
            entry("cambodia", "Cambodia"),
            entry("cameroon", "Cameroon"),
            entry("canada", "Canada"),
            entry("cayman-islands", "the Cayman Islands"),
            entry("chile", "Chile"),
            entry("china", "China"),
            entry("colombia", "Colombia"),
            entry("costa-rica", "Costa Rica"),
            entry("croatia", "Croatia"),
            entry("cyprus", "Cyprus"),
            entry("czech-republic", "the Czech Republic"),
            entry("dem-rep-of-the-congo", "Democratic Republic of the Congo"),
            entry("democratic-republic-of-the-congo", "Democratic Republic of the Congo"),
            entry("denmark", "Denmark"),
            entry("djibouti", "Djibouti"),
            entry("dominica", "the Dominican Republic"),
            entry("dominican-republic", "the Dominican Republic"),
            entry("ecuador", "Ecuador"),
            entry("egypt", "Egypt"),
            entry("el-salvador", "El Salvador"),
            entry("estonia", "Estonia"),
            entry("eswatini", "Eswatini"),
            entry("finland", "Finland"),
            entry("france", "France"),
            entry("germany", "Germany"),
            entry("ghana", "Ghana"),
            entry("gibraltar", "Gibraltar"),
            entry("greece", "Greece"),
            entry("guatemala", "Guatemala"),
            entry("honduras", "Honduras"),
            entry("hong-kong", "China"),
            entry("hungary", "Hungary"),
            entry("iceland", "Iceland"),
            entry("india", "India"),
            entry("indonesia", "Indonesia"),
            entry("iran", "Iran"),
            entry("iraq", "Iraq"),
            entry("ireland", "Ireland"),
            entry("israel", "Israel"),
            entry("italy", "Italy"),
            entry("jamaica", "Jamaica"),
            entry("japan", "Japan"),
            entry("jordan", "Jordan"),
            entry("kazakhstan", "Kazakhstan"),
            entry("kenya", "Kenya"),
            entry("kosovo", "Kosovo"),
            entry("kuwait", "Kuwait"),
            entry("latin-america", "Latin America"),
            entry("latvia", "Latvia"),
            entry("lebanon", "Lebanon"),
            entry("liechtenstein", "Liechtenstein"),
            entry("lithuania", "Lithuania"),
            entry("luxembourg", "Luxembourg"),
            entry("madagascar", "Madagascar"),
            entry("malawi", "Malawi"),
            entry("malaysia", "Malaysia"),
            entry("malta", "Malta"),
            entry("mauritius", "Mauritius"),
            entry("mexico", "Mexico"),
            entry("monaco", "Monaco"),
            entry("montenegro", "Montenegro"),
            entry("morocco", "Morocco"),
            entry("myanmar", "Myanmar"),
            entry("netherlands", "the Netherlands"),
            entry("new-zealand", "New Zealand"),
            entry("nigeria", "Nigeria"),
            entry("norway", "Norway"),
            entry("oman", "Oman"),
            entry("pakistan", "Pakistan"),
            entry("panama", "Panama"),
            entry("peru", "Peru"),
            entry("philippines", "the Philippines"),
            entry("poland", "Poland"),
            entry("portugal", "Portugal"),
            entry("qatar", "Qatar"),
            entry("romania", "Romania"),
            entry("russia", "Russia"),
            entry("rwanda", "Rwanda"),
            entry("saint-barthelemy", "Saint Barthelemy"),
            entry("saint-kitts-nevis", "Saint Kitts & Nevis"),
            entry("saudi-arabia", "Saudi Arabia"),
            entry("senegal", "Senegal"),
            entry("serbia", "Serbia"),
            entry("sierra-leone", "Sierra Leone"),
            entry("singapore", "Singapore"),
            entry("slovakia", "Slovakia"),
            entry("slovenia", "Slovenia"),
            entry("south-africa", "South Africa"),
            entry("south-korea", "Korea (South)"),
            entry("spain", "Spain"),
            entry("sri-lanka", "Sri Lanka"),
            entry("suriname", "Suriname"),
            entry("sweden", "Sweden"),
            entry("switzerland", "Switzerland"),
            entry("taiwan", "Taiwan"),
            entry("tanzania", "Tanzania"),
            entry("thailand", "Thailand"),
            entry("trinidad-tobago", "Trinidad & Tobago"),
            entry("tunisia", "Tunisia"),
            entry("turkey", "Turkey"),
            entry("turks-and-caicos-islands", "Turks and Caicos Islands"),
            entry("uae", "the UAE"),
            entry("uganda", "Uganda"),
            entry("ukraine", "Ukraine"),
            entry("uk", "England"),
            entry("united kingdom", "England"),
            entry("uruguay", "Uruguay"),
            entry("venezuela", "Venezuela"),
            entry("vietnam", "Vietnam"),
            entry("yemen", "Yemen"),
            entry("zambia", "Zambia"),
            entry("zimbabwe", "Zimbabwe")
    );

    public LeadersIn() {
        super(
                "Leaders In",
                "https://www.leaders-in-law.com/find-an-expert/",
                1,
                5
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

        MyDriver.clickOnElementMultipleTimes(
                By.xpath("//*[@id=\"load-more\"]/div/div/a"),
                5, 2.5
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("div.jet-listing-grid__item"));
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("a[href*='https://www.leaders-in-law.com/country/']")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }


    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.elementor-button")}, "LINK", "href", LawyerExceptions::linkException);
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h6.elementor-heading-title")}, "NAME", LawyerExceptions::nameException);
        String firm = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".elementor-widget-jet-listing-dynamic-terms")}, "FIRM", LawyerExceptions::nameException);
        String pa = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("a[href*='https://www.leaders-in-law.com/practice_area/']")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        String country = this.getCountry(lawyer);

        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name.replace("Name:", "").trim(),
                "role", "-----",
                "firm", firm.replace("Company:", "").trim(),
                "country", country,
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "441619094535" : socials[1]
        );
    }
}

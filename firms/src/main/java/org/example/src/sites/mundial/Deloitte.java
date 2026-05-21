package org.example.src.sites.mundial;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class Deloitte extends ByNewPage {

    public Deloitte() {
        super(
                "Deloitte",
                "https://www.itrworldtax.com/Firm/deloitte/Profile/89#lawyers",
                1,
                5
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("albania", "Albania"),
            entry("argentina", "Argentina"),
            entry("australia", "Australia"),
            entry("austria", "Austria"),
            entry("azerbaijan", "Azerbaijan"),
            entry("belgium", "Belgium"),
            entry("brazil", "Brazil"),
            entry("bulgaria", "Bulgaria"),
            entry("cambodia", "Cambodia"),
            entry("canada", "Canada"),
            entry("chile", "Chile"),
            entry("china", "China"),
            entry("colombia", "Colombia"),
            entry("costa rica", "Costa Rica"),
            entry("croatia", "Croatia"),
            entry("cyprus", "Cyprus"),
            entry("czech republic", "the Czech Republic"),
            entry("denmark", "Denmark"),
            entry("dominican republic", "the Dominican Republic"),
            entry("ecuador", "Ecuador"),
            entry("el salvador", "El Salvador"),
            entry("estonia", "Estonia"),
            entry("finland", "Finland"),
            entry("france", "France"),
            entry("germany", "Germany"),
            entry("ghana", "Ghana"),
            entry("greece", "Greece"),
            entry("guatemala", "Guatemala"),
            entry("honduras", "Honduras"),
            entry("hong kong sar", "China"),
            entry("hungary", "Hungary"),
            entry("india", "India"),
            entry("indonesia", "Indonesia"),
            entry("ireland", "Ireland"),
            entry("israel", "Israel"),
            entry("italy", "Italy"),
            entry("japan", "Japan"),
            entry("kenya", "Kenya"),
            entry("kosovo", "Kosovo"),
            entry("latvia", "Latvia"),
            entry("lithuania", "Lithuania"),
            entry("luxembourg", "Luxembourg"),
            entry("malaysia", "Malaysia"),
            entry("malta", "Malta"),
            entry("mexico", "Mexico"),
            entry("netherlands", "the Netherlands"),
            entry("new zealand", "New Zealand"),
            entry("nicaragua", "Nicaragua"),
            entry("nigeria", "Nigeria"),
            entry("norway", "Norway"),
            entry("peru", "Peru"),
            entry("philippines", "the Philippines"),
            entry("poland", "Poland"),
            entry("portugal", "Portugal"),
            entry("romania", "Romania"),
            entry("saudi arabia", "Saudi Arabia"),
            entry("serbia", "Serbia"),
            entry("singapore", "Singapore"),
            entry("slovak republic", "Slovak Republic"),
            entry("slovenia", "Slovenia"),
            entry("south africa", "South Africa"),
            entry("south korea", "Korea (South)"),
            entry("spain", "Spain"),
            entry("sri lanka", "Sri Lanka"),
            entry("sweden", "Sweden"),
            entry("switzerland", "Switzerland"),
            entry("taiwan", "Taiwan"),
            entry("thailand", "Thailand"),
            entry("türkiye", "Türkiye"),
            entry("ukraine", "Ukraine"),
            entry("united arab emirates", "the UAE"),
            entry("united kingdom", "England"),
            entry("uruguay", "Uruguay"),
            entry("venezuela", "Venezuela"),
            entry("vietnam", "Vietnam")
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.itrworldtax.com/Search/FirmLawyers/deloitte/89?firmIds=89&pageNumber=" + (index + 1) + "&pageSize=25&Keyword=&SearchAll=False&SearchFirms=False&SearchLawyers=False&SearchNews=False&SearchJurisdictions=False&SearchDeals=False&SortOrder=ByLawyersPredefinedOrder&JurisdictionIds=&PracticeAreaIds=&IndustrySectorIds=&ArticleTypeIds=&BarAdmissionIds=&LawyerRankingIds=&FirmIds=89&DealTypeIds=&DealYearIds=&SearchValueFrom=0&SearchValueTo=0&DealValueIds=&LawyerIds=";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("div.lawyersList a.profile"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = driver.findElement(By.xpath("/html/body/div[6]/div[2]/div[2]/div[1]/div[1]/div[2]/div[1]/p/a")).getAttribute("textContent");
        if (country.isEmpty()) country = lawyer.getAttribute("innerText");
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }


    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h4")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div[6]/div[2]/div[2]/div[1]/div[1]"));
        String practiceArea = driver.findElement(By.xpath("/html/body/div[6]/div[2]/div[2]/div[1]/div[1]/div[2]/div[2]/p")).getText();
        String country = getCountry(lawyer);

        String[] socials = this.getSocials(container.findElements(By.cssSelector(".basicInfo a")), false);
        socials[1] = super.getSocialsFromText(container.getText())[1];

        return Map.of(
                "link", link,
                "name", name,
                "role", "-----",
                "firm", this.name,
                "country", country,
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1]
        );
    }
}

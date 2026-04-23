package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class VSV extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "costa rica", "Costa Rica",
            "el salvador", "El Salvador",
            "guatemala", "Guatemala",
            "honduras", "Honduras"
    );
    
    public VSV() {
        super(
                "VSV",
                "https://altalegal.com/en/profesionales/",
                5
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://altalegal.com/en/profesionales/?e-page-a538f2d=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.e-loop-item"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }
    
    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String country = extractor.extractLawyerText(lawyer, new By[] {By.cssSelector("a[href*='https://altalegal.com/en/pais/']")}, "COUNTRY", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Costa Rica");
    }
    

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("elementor-heading-title")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[href*='https://altalegal.com/en/cargo/']")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String country = getCountry(lawyer);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//*[@id=\"content\"]/div/section[1]/div/div[1]/div/div[4]"));
        String[] split = container.getText().split("\n");

        String email = split[0];
        String phone = split[1];

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "50640362000" : phone
        );
    }
}

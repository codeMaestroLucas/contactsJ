package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class BDSAsesores extends ByNewPage {

    public BDSAsesores() {
        super(
                "BDS Asesores",
                "https://www.bdsasesores.com/en/team/",
                1
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "costa rica", "Costa Rica",
            "panama", "Panama",
            "dominican republic", "the Dominican Republic",
            "el salvador", "El Salvador",
            "guatemala", "Guatemala",
            "honduras", "Honduras",
            "nicaragua", "Nicaragua"
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.member-team"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("member-job")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getCountry(String country) {
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "Costa Rica");
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("member-job")}, "ROLE", LawyerExceptions::roleException);
        String country = getCountry(role);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("et_pb_team_member_description"));

        String[] socials = null;
        socials = super.getSocials(container.findElements(By.tagName("a")), false);
        if (socials[0].isEmpty()) {
            socials = super.getSocialsFromText(container.getText());
        }

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "Labor & Employment",
                "email", socials[0],
                "phone", "50625453600"
        );
    }
}

package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BabicAndPartners extends ByPage {

    public BabicAndPartners() {
        super(
                "Babic & Partners",
                "",
                9
        );
    }

    String[] links = {
            "https://www.babic-partners.hr/legal-team/stanislav-babic/",
            "https://www.babic-partners.hr/legal-team/marijana-baricevic/",
            "https://www.babic-partners.hr/legal-team/iva-basaric/",
            "https://www.babic-partners.hr/legal-team/nenad-belosa/",
            "https://www.babic-partners.hr/legal-team/marija-gregoric/",
            "https://www.babic-partners.hr/legal-team/nenad-grof/",
            "https://www.babic-partners.hr/hr/pravnicki-tim/mihael-martincic/",
            "https://www.babic-partners.hr/legal-team/matija-skender/",
            "https://www.babic-partners.hr/legal-team/marta-telebuh/",
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(links[index]);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("div.article_content_left"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div.article_content_left p")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String[] socials = super.getSocialsFromText(lawyer.getText());

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("entry-title")}, "NAME", LawyerExceptions::nameException),
                "role", role,
                "firm", this.name,
                "country", "Croatia",
                "practice_area", "",
                "email", socials[0],
                "phone", "38514821211"
        );
    }
}

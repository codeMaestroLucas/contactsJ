package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class TRINITILawFirm extends ByNewPage {

    public TRINITILawFirm() {
        super(
                "TRINITI Law Firm",
                "https://triniti.eu/people/?_sft_person_positions=partner",
                1,
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.rollDown(5, 0.5);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("div.single-person"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.person-wrapper")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException);
        String country = extractor.extractLawyerText(lawyer, new By[]{By.className("country-tag")}, "COUNTRY", LawyerExceptions::countryException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("person-contacts"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", country,
                "practice_area", container.getText(),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}

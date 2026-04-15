package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class AzevedoSette extends ByPage {

    public AzevedoSette() {
        super(
                "Azevedo Sette",
                "https://www.azevedosette.com.br/equipe/en",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("section.b-team"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String vcardUrl = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[href*='.vcf']")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] vcardData = VCard.withDefaultPatterns().getSocials(vcardUrl);
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h3.b-team__name").xpath("./..")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h3.b-team__name")}, "NAME", LawyerExceptions::nameException),
                "role", "Lawyer",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.className("b-team__category")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0].isEmpty() ? vcardData[0] : socials[0],
                "phone", vcardData[1].isEmpty() ? "551140837600" : vcardData[1]
        );
    }
}
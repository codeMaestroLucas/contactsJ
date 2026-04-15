package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class TRINITILawFirm extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public TRINITILawFirm() {
        super(
                "TRINITI Law Firm",
                "https://triniti.eu/people/?_sft_person_positions=partner",
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

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("person-contacts"));
        String vcardHref = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href*='.vcf']")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] socialsVCard = vCard.getSocials(vcardHref);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Lithuania",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath("//h4[text()='Services']/../../..//div[@class='contacts']")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0].isEmpty() ? socialsVCard[0] : socials[0],
                "phone", socials[1].isEmpty() ? socialsVCard[1] : socials[1]
        );
    }
}

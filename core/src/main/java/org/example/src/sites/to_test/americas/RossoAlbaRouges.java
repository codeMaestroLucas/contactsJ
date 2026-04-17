package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class RossoAlbaRouges extends ByPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public RossoAlbaRouges() {
        super(
                "Rosso Alba & Rougès",
                "https://www.rossoalba.com/professionals/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".et_pb_team_member"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("et_pb_member_position")}, true);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String vcardHref = extractor.extractLawyerAttribute(lawyer, new By[]{By.xpath("./ancestor::div[contains(@class,'et_pb_column')]//a[contains(@href, '.vcf')]")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] socials = vCard.getSocials(vcardHref);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("et_pb_module_header")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("et_pb_member_position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Argentina",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "541148192600" : socials[1]
        );
    }
}

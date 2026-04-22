package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class FBPHLaw extends ByNewPage {

    public FBPHLaw() {
        super(
                "FBPH Law",
                "https://fbphlaw.com/en/our-team/",
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
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("et_pb_member_position")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer.findElement(By.xpath("./../..")), new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h4")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("et_pb_member_position")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("service_sidebar"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String phone = extractor.extractLawyerText(container, new By[]{By.xpath("//div[span[text()='Telephone']]//span[@class='cmsms_features_item_desc']")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Ecuador",
                "practice_area", extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("//footer/div[2]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", phone.isEmpty() ? "59322986456" : phone
        );
    }
}

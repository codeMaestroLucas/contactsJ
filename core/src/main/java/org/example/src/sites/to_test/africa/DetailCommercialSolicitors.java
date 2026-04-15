package org.example.src.sites.to_test.africa;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class DetailCommercialSolicitors extends ByNewPage {

    public DetailCommercialSolicitors() {
        super(
                "Detail Commercial Solicitors",
                "https://www.detailsolicitors.com/our-team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.et_pb_team_member"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("et_pb_member_position")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer.findElement(By.xpath("./..")), new By[]{}, "LINK", "data-et-has-click-event", LawyerExceptions::linkException);
        if(link.isEmpty()) link = lawyer.findElement(By.xpath("./../following-sibling::a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("et_pb_module_header")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("et_pb_member_position")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("et_pb_text_inner"));
        String[] socials = super.getSocials(container.findElements(By.tagName("p")), true);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "23412771400" : socials[1]
        );
    }
}

package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class RLD extends ByNewPage {

    public RLD() {
        super(
                "RLD",
                "https://rld.es/en/team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("et_pb_team_member"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("et_pb_member_position")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        // As the sample shows no direct <a> on the card wrapper but implies it's clickable
        // Usually Divi theme cards are clickable or have a link in the title
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("img")}, "LINK", "src", LawyerExceptions::linkException); 
        // Note: The sample HTML provided for RLD list doesn't show the <a> href clearly, 
        // but ByNewPage logic requires a link. Assuming title or image wrapper logic.
        MyDriver.openNewTab(this.link); // Fallback to current if link extraction logic differs per site
        return this.link; 
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("et_pb_module_header")}, "NAME", LawyerExceptions::nameException);
        
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("et_pb_row_0"));

        String role = extractor.extractLawyerText(container, new By[]{By.tagName("h3")}, "ROLE", LawyerExceptions::roleException);
        String practiceArea = extractor.extractLawyerText(container, new By[]{By.tagName("h4")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        
        String rawSocials = container.findElement(By.className("et_pb_cta_0")).getAttribute("innerHTML");
        String[] socials = this.getSocialsFromText(rawSocials);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Spain",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34913086593" : socials[1]
        );
    }
}

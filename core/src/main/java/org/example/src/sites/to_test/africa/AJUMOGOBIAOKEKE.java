package org.example.src.sites.to_test.africa;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class AJUMOGOBIAOKEKE extends ByNewPage {

    public AJUMOGOBIAOKEKE() {
        super(
                "AJUMOGOBIA & OKEKE",
                "https://www.ajumogobiaokeke.com/people/",
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
        return MyDriver.wait.findElements(By.cssSelector(".ult-team-member-wrap"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("ult-team-member-name")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("ult-content-box"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("uvc-sub-heading")}, "ROLE", LawyerExceptions::roleException).split("\n")[0];
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String[] socials = super.getSocialsFromText(extractor.extractLawyerText(container, new By[]{By.className("uvc-sub-heading")}, "SOCIALS", LawyerExceptions::socialsException));

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "+234-1-2771325" : socials[1]
        );
    }
}

package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class SKS extends ByPage {

    public SKS() {
        super(
                "SK&S Law Firm",
                "https://skslegal.pl/en/team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.rollDownToBottom(0.4);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("inline-column"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("team-person-name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("team-person-position")}, "ROLE", LawyerExceptions::roleException);
        
        WebElement emailNode = lawyer.findElement(By.className("hidden-email"));
        String email = emailNode.getAttribute("data-name") + "@" + emailNode.getAttribute("data-domain");
        String phone = extractor.extractLawyerText(lawyer, new By[]{By.className("team-person-phone")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("team-person-link")}, "LINK", "href", LawyerExceptions::linkException),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Poland",
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "48226087000" : phone
        );
    }
}

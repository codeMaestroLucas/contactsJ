package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BurnessPaull extends ByNewPage {

    public BurnessPaull() {
        super(
                "Burness Paull",
                "https://www.burnesspaull.com/people/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("ui-content-card[look='peopleCard']"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("h3")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "hyperlink", LawyerExceptions::linkException);
        MyDriver.openNewTab("https://www.burnesspaull.com" + url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h2")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String practiceArea = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("div[slot='body'] p")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);
        WebElement contactCard = MyDriver.wait.findElement(By.xpath("//*[@id=\"personal-profile-container\"]/div/div/div/ui-person-card/ui-contact-card"));

        String email = extractor.extractLawyerAttribute(contactCard, new By[]{}, "EMAIL", "emailaddress", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerAttribute(contactCard, new By[]{}, "PHONE", "telephone", LawyerExceptions::phoneException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "England",
                "practice_area", practiceArea,
                "email", email,
                "phone", phone.isEmpty() ? "441314736189" : phone
        );
    }
}

package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class VieiraRezendeAdvogados extends ByNewPage {

    private final By[] byRoleArray = {
            By.cssSelector(".cargo span")
    };

    public VieiraRezendeAdvogados() {
        super(
                "Vieira Rezende Advogados",
                "https://www.vieirarezende.com.br/en/profissionais/",
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.vieirarezende.com.br/en/profissionais/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("team-card"));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement infoLawyer = driver.findElement(By.className("info-lawyer"));
        WebElement expertDiv = driver.findElement(By.className("related-areas"));
        WebElement contactInfo = driver.findElement(By.className("contact-info"));

        String practiceArea = extractor.extractLawyerText(expertDiv, new By[]{By.tagName("a")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        String[] socials = super.getSocials(contactInfo.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551133301010" : socials[1]
        );
    }
}

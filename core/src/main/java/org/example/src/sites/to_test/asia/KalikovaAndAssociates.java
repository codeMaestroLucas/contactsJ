package org.example.src.sites.to_test.asia;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class KalikovaAndAssociates extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public KalikovaAndAssociates() {
        super(
                "KalikovaAndAssociates",
                "http://www.k-a.kg/eng/persons",
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
        return MyDriver.wait.findElements(By.cssSelector("div.views-row"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".views-field-title a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String nameRoleRaw = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".views-field-title a")}, "NAME_ROLE", LawyerExceptions::nameException);
        String name = nameRoleRaw.contains("-") ? nameRoleRaw.split("-")[1].trim() : nameRoleRaw;

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("levaya-kolonka"));
        String role = extractor.extractLawyerText(container, new By[]{By.className("doljnost-persona")}, "ROLE", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String vcardHref = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href*='.vcf']")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] socials = vCard.getSocials(vcardHref);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Kyrgyzstan",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("sfera-deyatelnosti-persona")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "996312666060" : socials[1]
        );
    }
}

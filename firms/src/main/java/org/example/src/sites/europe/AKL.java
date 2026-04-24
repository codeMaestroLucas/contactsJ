package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class AKL extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public AKL() {
        super(
                "AKL",
                "https://www.aklawfirm.gr/team",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".views-row"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("position")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("title")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("position")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        String[] socials = null;
        try {
            WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div[2]/div/main/div[2]/article/div[2]/div[2]/div/div[5]/div/div"));
            String vcardHref = extractor.extractLawyerAttribute(container, new By[]{By.linkText("vCard")}, "VCARD", "href", LawyerExceptions::socialsException);
            socials = vCard.getSocials(vcardHref);
            if (socials[0].isEmpty()) throw new LawyerExceptions("No social found");
        } catch (Exception e) {
            WebElement container = MyDriver.wait.findElement(By.tagName("body"));
            socials = super.getSocialsFromText(container.getText());
        }

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Greece",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "302103358700" : socials[1]
        );
    }
}

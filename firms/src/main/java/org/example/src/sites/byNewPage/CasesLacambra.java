package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class CasesLacambra extends ByNewPage {

    public CasesLacambra() {
        super(
                "Cases & Lacambra",
                "https://www.caseslacambra.com/professionals/",
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.caseslacambra.com/professionals/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("c-professionals-list__professional-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("c-professionals-list__professional-role")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileLink = lawyer.getAttribute("href");
        MyDriver.openNewTab(profileLink);
        return profileLink;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("s-template-professional"));

        String name = extractor.extractLawyerText(container, new By[]{By.className("c-professional-header__name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.className("c-professional-header__subheading")}, "ROLE", LawyerExceptions::roleException);

        String[] socials = super.getSocials(container.findElement(By.className("c-professional-contact")).findElements(By.tagName("a")), false);
        String phone = extractor.extractLawyerText(container, new By[]{By.className("c-professional-contact__item-wrapper")}, "PHONE", LawyerExceptions::phoneException);
        String country = phone.startsWith("34") ? "Spain" : "USA";

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", phone.isEmpty() ? "376728001" : phone
        );
    }
}

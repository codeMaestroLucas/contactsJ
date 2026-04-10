package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MundieEAdvogados extends ByNewPage {

    public MundieEAdvogados() {
        super(
                "Mundie e Advogados",
                "https://www.mundie.com.br/en/profissionais",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(2000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("gallery-item-container"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("info-element-title")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.clickOnElement(lawyer.findElement(By.className("item-action")));
        MyDriver.waitForPageToLoad();
        return driver.getCurrentUrl();
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("info-element-description")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("info-element-title")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.className("ku3DBC"));

        String[] socials = super.getSocials(driver.findElements(By.cssSelector("a[href^='mailto:']")), false);
        String phoneText = extractor.extractLawyerText(driver.findElement(By.id("comp-ltrni0dm")), new By[]{By.tagName("span")}, "PHONE", LawyerExceptions::phoneException);
        String phone = super.getSocialsFromText(phoneText)[1];

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", extractor.extractLawyerText(driver.findElement(By.id("comp-mgi828gh")), new By[]{By.tagName("p")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", phone.isEmpty() ? "551130402900" : phone
        );
    }
}

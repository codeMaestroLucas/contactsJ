package org.example.src.sites.americas;

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
        MyDriver.rollDownToBottom(0.5);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"sócio", "sócia"};
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("item-link-wrapper"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("info-element-title")}, true, validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.cmdClickOnElement(lawyer.findElement(By.className("item-action")));
        return driver.getCurrentUrl();
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);

        String text = MyDriver.wait.findElement(By.id("SITE_CONTAINER")).getText();
        String[] socials = super.getSocialsFromText(text);


        return Map.of(
                "link", link,
                "name", "",
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", text,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551130402900" : socials[1]
        );
    }
}

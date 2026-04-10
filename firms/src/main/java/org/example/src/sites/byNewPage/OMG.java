package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class OMG extends ByNewPage {

    public OMG() {
        super(
                "OMG",
                "https://www.omg.com.do/talentos?lang=en",
                10
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        } else {
            MyDriver.clickOnElement(By.xpath("//*[@id=\"comp-k9k0hfh7\"]/div[3]"));
            Thread.sleep(3000);
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("_FiCX"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("[id*='comp-js0gv38a']")}, true);
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("[id*='comp-js0gv36c']")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("[id*='comp-js0gv38a']")}, "ROLE", LawyerExceptions::roleException);
        String pa = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("[id*='comp-js0gv3bf']")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("//*[@id=\"comp-jrrv3o7v\"]/div[3]"));
        String[] socials = super.getSocialsFromText(container.getText());
        String phone = extractor.extractLawyerText(container, new By[]{By.id("comp-jrrvf62n")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Dominican Republic",
                "practice_area", pa,
                "email", socials[0],
                "phone", phone.isEmpty() ? "8093810505" : phone
        );
    }
}

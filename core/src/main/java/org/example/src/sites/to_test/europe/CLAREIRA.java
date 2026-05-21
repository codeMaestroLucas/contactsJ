package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class CLAREIRA extends ByPage {

    public CLAREIRA() {
        super(
                "CLAREIRA",
                "https://www.clareira.com/equipa/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div[data-bl-name='Card Equipa']"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("div[data-bl-name='Job'] div")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("img")}, "NAME", "alt", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div[data-bl-name='Job'] div")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div[data-bl-name='Email'] .bl-dyn")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div[data-bl-name='Telephone'] .bl-dyn")}, "PHONE", LawyerExceptions::phoneException);
        String relativeLink = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[data-bl-name='Link']")}, "LINK", "href", LawyerExceptions::linkException);

        return Map.of(
                "link", relativeLink,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Portugal",
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "351213400804" : phone
        );
    }
}

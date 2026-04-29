package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Demarest extends ByNewPage {

    public Demarest() {
        super(
                "Demarest",
                "https://www.demarest.com.br/en/professionals/",
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
        return MyDriver.wait.findElements(By.className("profissional-lista-single"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        // Site uses onclick for navigation, but we can infer the slug or wait for the URL
        lawyer.click();
        MyDriver.waitForPageToLoad();
        return driver.getCurrentUrl();
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("titulo")}, "NAME", LawyerExceptions::nameException);
        
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("menor-profissionais"));

        String role = extractor.extractLawyerText(container, new By[]{By.tagName("p")}, "ROLE", LawyerExceptions::roleException);
        String practiceArea = extractor.extractLawyerText(container, new By[]{By.cssSelector("a[href*='areas-de-atuacao']")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        
        String[] socials = this.getSocials(container.findElements(By.cssSelector(".dados a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551133561800" : socials[1]
        );
    }
}

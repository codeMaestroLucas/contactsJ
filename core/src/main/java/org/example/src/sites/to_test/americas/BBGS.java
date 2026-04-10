package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class BBGS extends ByNewPage {

    private String currentCountry = "";

    public BBGS() {
        super("BBGS", "https://www.bbgslegal.com/chile?lang=en", 4);
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String[] links = {
                "https://www.bbgslegal.com/chile?lang=en",
                "https://www.bbgslegal.com/equipo-col?lang=en",
                "https://www.bbgslegal.com/mexico?lang=en",
                "https://www.bbgslegal.com/peru?lang=en"
        };
        String[] countries = {"Chile", "Colombia", "Mexico", "Peru"};

        this.currentCountry = countries[index];
        this.driver.get(links[index]);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div[role='listitem'] div.YzqVVZ"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.cssSelector("div[id*='comp-lws6hanu'] p")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("p span span")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div[id*='comp-lws6hanu'] p")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.cssSelector("div[id*='comp-lwiolmu1']"));
        String[] socials = super.getSocialsFromText(extractor.extractLawyerText(container, new By[]{By.tagName("p")}, "SOCIALS", LawyerExceptions::socialsException));

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", this.currentCountry,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxx" : socials[1]
        );
    }
}

package org.example.src.sites.americas;

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
        super("BBGS", "https://www.bbgslegal.com/chile?lang=en", 4, 2);
    }

    private final String[] links = {
            "https://www.bbgslegal.com/chile?lang=en",
            "https://www.bbgslegal.com/equipo-col?lang=en",
            "https://www.bbgslegal.com/mexico?lang=en",
            "https://www.bbgslegal.com/peru?lang=en"
    };

    private final String[] countries = {"Chile", "Colombia", "Mexico", "Peru"};

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.currentCountry = countries[index];
        this.driver.get(links[index]);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        By selector = By.cssSelector("div.wixui-repeater > div[role='list'] div[role='listitem']");
        List<WebElement> lawyers = MyDriver.wait.findElements(selector);
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("div")}, true);

    }

    @Override
    public String openNewTab(WebElement lawyer) {
        String link = null;
        try {
            link = lawyer.findElement(By.tagName("a")).getAttribute("href");
            MyDriver.openNewTab(link);
        } catch (Exception e) {
            MyDriver.cmdClickOnElement(lawyer);
            link = driver.getCurrentUrl();
        }
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = null;
        String role = null;
        try {
            name = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("p a[href*='https://www.bbgslegal.com/']")}, "NAME","textContent" , LawyerExceptions::nameException);
            role = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("p > span")}, "ROLE","textContent" , LawyerExceptions::roleException);
        } catch (LawyerExceptions e) {
            role = name = lawyer.getText();
        }

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.tagName("main"));
        String s = extractor.extractLawyerAttribute(container, new By[]{By.tagName("p")}, "SOCIALS","textContent" ,LawyerExceptions::socialsException);
        String[] socials = super.getSocialsFromText(container.getText());

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

package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Heussen extends ByNewPage {

    public Heussen() {
        super(
                "Heussen",
                "https://www.heussen-law.nl/en/team",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("li.lawyer_card_wrap"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("lawyer_card_text_title")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div/div/div[1]/div[1]/div[3]"));
        String[] socials = super.getSocialsFromText(container.getText());
        socials[1] = super.getSocials(container.findElements(By.tagName("p")), true)[1];


        String pa = null;
        try {
            pa = driver.findElement(By.cssSelector("a[href*='/en/practices/practice-groups/']")).getText();
        } catch (Exception e) {}

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31203122800" : socials[1]
        );
    }
}

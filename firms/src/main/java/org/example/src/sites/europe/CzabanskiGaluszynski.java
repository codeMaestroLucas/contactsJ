package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class CzabanskiGaluszynski extends ByNewPage {

    public CzabanskiGaluszynski() {
        super(
                "Czabański Gałuszyński",
                "https://czabanski-galuszynski.pl/en/lawyers/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.elementor-widget-image-box"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("elementor-image-box-description")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-image-box-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-image-box-description")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        String[] socials = null;
        try {
            WebElement container = MyDriver.wait.findElement(By.xpath("//*[@id=\"content\"]/div/div[1]/section/div/div[2]/div/div[4]/div/ul"));
            socials = super.getSocialsFromText(container.getText());
        } catch (Exception e) {
            WebElement element = MyDriver.wait.findElement(By.xpath("//*[@id=\"content\"]/div/div[1]/section/div/div[2]/div"));
            WebElement container = element.findElement(By.tagName("ul"));
            socials = super.getSocialsFromText(container.getText());

        }

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Poland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "48226252919" : socials[1]
        );
    }
}

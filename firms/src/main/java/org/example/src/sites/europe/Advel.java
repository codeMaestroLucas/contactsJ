package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Advel extends ByPage {

    public Advel() {
        super(
                "Advel",
                "https://advel.is/en/home/starfsfolk/",
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
        WebElement div = MyDriver.wait.findElement(By.xpath("/html/body/div[2]/div/section[2]/div/div/div/div/div/div/div/section[3]/div/div/div"));
        List<WebElement> lawyers = div.findElements(By.className("elementor-inner-column"));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocialsFromText(lawyer.getText());

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h5.elementor-image-box-title a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h5.elementor-image-box-title a")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-image-box-description")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Iceland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "3545905000" : socials[1]
        );
    }
}

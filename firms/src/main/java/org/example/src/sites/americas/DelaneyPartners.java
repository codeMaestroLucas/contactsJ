package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DelaneyPartners extends ByPage {

    public DelaneyPartners() {
        super(
                "Delaney Partners",
                "",
                9
        );
    }

    String[] links = {
            "https://delaneypartners.com/attorneys/robert-k-adams/",
            "https://delaneypartners.com/attorneys/lena-m-bonaby/",
            "https://delaneypartners.com/attorneys/samuel-r-brown/",
            "https://delaneypartners.com/attorneys/jillian-chase-jones/",
            "https://delaneypartners.com/attorneys/john-delaney-bahamas/",
            "https://delaneypartners.com/attorneys/bryann-v-hepburn/",
            "https://delaneypartners.com/attorneys/pamela-l-klonaris/",
            "https://delaneypartners.com/attorneys/edward-j-marshall-ii/",
            "https://delaneypartners.com/attorneys/sofia-j-papageorge/"
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(links[index]);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("div.content"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h5")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "the Bahamas",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//p[text()='EXPERTISE']/following-sibling::ul[1]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "12427024500" : socials[1]
        );
    }
}

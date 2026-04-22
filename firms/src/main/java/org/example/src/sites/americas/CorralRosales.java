package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class CorralRosales extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public CorralRosales() {
        super(
                "Corral Rosales",
                "https://corralrosales.com/en/partners/",
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
        return MyDriver.wait.findElements(By.cssSelector(".hover-color-img"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("themestek-box-content"));

        String name = driver.findElement(By.xpath("//*[@id=\"main\"]/div/div[1]/div[2]/h3")).getText();
        String vcardHref = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href*='.vcf']")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] vcardData = vCard.getSocials(vcardHref);
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Ecuador",
                "practice_area", "",
                "email", socials[0].isEmpty() ? vcardData[0] : socials[0],
                "phone", socials[1].isEmpty() ? vcardData[1] : socials[1]
        );
    }
}

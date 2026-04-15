package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class NicholsonCano extends ByNewPage {

    public NicholsonCano() {
        super(
                "Nicholson & Cano",
                "https://nicholsonycano.com.ar/en/partners/",
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
        return MyDriver.wait.findElements(By.cssSelector("div.pt-cv-content-item"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("pt-cv-title")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("vc_column-inner"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String phone = extractor.extractLawyerText(container, new By[]{By.xpath("//h6[contains(.,'Contact')]/following-sibling::ul")}, "PHONE", LawyerExceptions::socialsException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Argentina",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath("//h6[contains(.,'Practice areas')]/following-sibling::ul")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", phone.isEmpty() ? "541148721600" : phone
        );
    }
}

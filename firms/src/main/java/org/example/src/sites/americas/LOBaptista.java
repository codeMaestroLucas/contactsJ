package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class LOBaptista extends ByPage {

    public LOBaptista() {
        super(
                "L O Baptista",
                "https://www.baptista.com.br/our-professionals/?lang=en",
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
        WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"sec-profissionais\"]/div/div/div"));
        return div.findElements(By.cssSelector("div.jet-listing-grid__item"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.className("jet-listing-dynamic-field__content")), true);
        socials[0] = super.getSocialsFromText(lawyer.getAttribute("innerHTML"))[0];


        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.className("jet-listing-dynamic-link__link")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("jet-listing-dynamic-link__label")}, "NAME", LawyerExceptions::nameException),
                "role", "",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-post-info__terms-list")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551131470800" : socials[1]
        );
    }
}

package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class AraujoPolicastro extends ByPage {

    public AraujoPolicastro() {
        super(
                "Araújo e Policastro",
                "https://araujopolicastro.com.br/en/equipe/?cargo=partners",
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
        return MyDriver.wait.findElements(By.cssSelector("div.lawyers-item"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String vcardUrl = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[download]")}, "VCARD", "href", LawyerExceptions::socialsException);
        String[] socials = VCard.withDefaultPatterns().getSocials(vcardUrl);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h2"), By.xpath("./..")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException),
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("div.lawyers-item-content p")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551130495700" : socials[1]
        );
    }
}

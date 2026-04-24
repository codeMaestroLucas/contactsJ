package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class SPVS extends ByPage {

    public SPVS() {
        super(
                "SPVS",
                "https://spvs.ro/teodora-stoian-en/",
                5
        );
    }

    String[] links = {
            "https://spvs.ro/teodora-stoian-en/",
            "https://spvs.ro/liria-poenaru-en/",
            "https://spvs.ro/alexandru-volcov-en/",
            "https://spvs.ro/liviu-samoila-en/",
            "https://spvs.ro/diana-vlasceanu-en/"
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(links[index]);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.tagName("body"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//p[contains(text(), 'Partner')]")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", this.link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Romania",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "40729822500" : socials[1]
        );
    }
}

package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class RomeuAmaralAdvogados extends ByNewPage {

    public RomeuAmaralAdvogados() {
        super(
                "Romeu Amaral Advogados",
                "https://romeuamaral.com.br/en/#team",
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
        try {
            return MyDriver.wait.findElements(By.cssSelector("div.item-equipe a.w-100"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("column-infos-links"));
        String vcard = null;

        try {
            vcard = driver.findElement(By.xpath("/html/body/section[3]/div/div/div[3]/div[2]/a[2]")).getAttribute("href");
        } catch (Exception e) {
            return "Invalid Role";
        }

        String[] socials = VCard.withDefaultPatterns().getSocials(vcard);
        String practice = extractor.extractLawyerText(container, new By[]{By.className("link-list")}, "PRACTICE", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551151805260" : socials[1]
        );
    }
}

package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class TanoiraCassagne extends ByNewPage {

    public TanoiraCassagne() {
        super(
                "Tanoira Cassagne",
                "https://www.tanoiracassagne.com/en/perfil/",
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        } else {
            MyDriver.rollDownToBottom(0.1);
            MyDriver.clickOnElement(By.xpath("/html/body/div[2]/section[3]/div/div/div/div[2]/div/div/div/div[6]/div"));
            Thread.sleep(1500);
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".jet-listing-grid__item"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("jet-listing-dynamic-terms__link")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h2 a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h2 a")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("jet-listing-dynamic-terms__link")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("/html/body/div[2]/section[1]/div/div/div/section/div/div[2]/div/div[7]/div/ul"));
        String[] socials = super.getSocialsFromText(container.getText());
        if (socials[0].isEmpty()) {
            socials = super.getSocialsFromText(container.getAttribute("innerText"));
        }

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Argentina",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "541152727100" : socials[1]
        );
    }
}

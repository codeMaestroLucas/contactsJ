package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class KaratzasAndPartners extends ByNewPage {

    public KaratzasAndPartners() {
        super(
                "Karatzas & Partners",
                "https://karatza-partners.gr/ourpeople/",
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
        return MyDriver.wait.findElements(By.cssSelector("a[href*='https://karatza-partners.gr/ourpeople/']"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);

        String role = extractor.extractLawyerAttribute(MyDriver.wait.findElement(By.tagName("body")), new By[]{By.xpath("/html/body/div[1]/app-component/div/div[4]/app-component/section/div/div[2]/div[1]/body_1")}, "ROLE", "textContent", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String email = MyDriver.wait.findElement(By.xpath("/html/body/div[1]/app-component/div/div[4]/app-component/section/div/div[2]/div[2]/div[2]/a[1]/span[2]")).getAttribute("textContent");

        String name = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("/html/body/div[1]/app-component/div/div[4]/app-component/section/div/div[2]/div[1]/div[1]")}, "NAME", LawyerExceptions::nameException);
        String pa = extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[]{By.xpath("/html/body/div[1]/app-component/div/div[4]/app-component/section/div/div[2]/h4/span/p[2]")}, "ROLE", "textContent", LawyerExceptions::roleException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Greece",
                "practice_area", pa,
                "email", email,
                "phone", "302103713600"
        );
    }
}

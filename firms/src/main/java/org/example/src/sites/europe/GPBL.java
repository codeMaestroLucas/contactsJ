package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class GPBL extends ByNewPage {

    public GPBL() {
        super(
                "GPBL",
                "https://www.gpblex.it/en/people/",
                11
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        } else {
            MyDriver.clickOnElement(By.xpath("//*[@id=\"ajaxsearchprores18_1\"]/nav[2]/a[2]"));
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("div.item.asp_isotopic_item"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h3")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//*[@id=\"et-boc\"]/div/div/div[2]/div/div[2]"));

        String role = container.getAttribute("textContent");
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Italy",
                "practice_area", "",
                "email", socials[0],
                "phone", "390230302400"
        );
    }
}

package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class FabregaMolinoMulino extends ByNewPage {

    public FabregaMolinoMulino() {
        super(
                "Fabrega Molino & Mulino",
                "https://fmm.com.pa/our-people/",
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
        } else {
            String xpath = "//*[@id=\"extp-771\"]/div[4]/div/div/a[" + (index + 1) + "]";
            MyDriver.clickOnElement(By.xpath(xpath));
            Thread.sleep(3000);
        }
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("item-grid"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("h5")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h5")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("gdlr-core-pbf-column-content"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Panama",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "5073016600" : socials[1]
        );
    }
}

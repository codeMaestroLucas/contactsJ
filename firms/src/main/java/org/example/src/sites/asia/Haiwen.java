package org.example.src.sites.asia;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Haiwen extends ByNewPage {

    public Haiwen() {
        super(
                "Haiwen",
                "http://www.haiwen-law.com/64/",
                9
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "http://www.haiwen-law.com/64/pn" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("ul.wow > li"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("zhiw")}, true);
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
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("zwname")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("zhiw")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String countryTxt = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("wz")}, "COUNTRY", "textContent", LawyerExceptions::countryException);
        String country = countryTxt.toLowerCase().contains("hong kong") ? "Hong Kong" : "China";
        String practice = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("info")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("boxs"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", country,
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "861085606888" : socials[1]
        );
    }
}

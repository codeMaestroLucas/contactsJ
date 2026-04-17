package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class BakerTilly extends ByNewPage {

    public BakerTilly() {
        super(
                "Baker Tilly",
                "https://www.bakertilly.mx/en/about-us/our-team",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.flex.flex-col.rounded"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.xpath(".//div[contains(@class, 'mb-16') and not(contains(@class, 'text-xs'))]")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("font-headingBold")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.xpath(".//div[contains(@class, 'mb-16') and not(contains(@class, 'text-xs'))]")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.cssSelector("div.md\\:w-2\\/3"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath(".//div[span[contains(text(), 'Legal')]]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", extractor.extractLawyerText(container, new By[]{By.xpath(".//span[contains(text(), 'T:')]")}, "PHONE", LawyerExceptions::phoneException).replace("T:", "").trim()
        );
    }
}

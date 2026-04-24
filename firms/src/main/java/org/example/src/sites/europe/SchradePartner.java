package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class SchradePartner extends ByNewPage {

    public SchradePartner() {
        super(
                "SCHRADE & PARTNER",
                "https://schrade-partner.de/team/",
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
        WebElement div = MyDriver.wait.findElement(By.xpath("/html/body/div[2]/section[2]"));
        List<WebElement> lawyers = div.findElements(By.cssSelector("div.elementor-column.elementor-col-25.elementor-inner-column"));

        div = driver.findElement(By.xpath("/html/body/div[2]/section[4]"));
        lawyers.addAll(div.findElements(By.cssSelector("div.elementor-column.elementor-col-25.elementor-inner-column")));

        div = driver.findElement(By.xpath("/html/body/div[2]/section[6]"));
        lawyers.addAll(div.findElements(By.cssSelector("div.elementor-column.elementor-col-25.elementor-inner-column")));

        div = driver.findElement(By.xpath("/html/body/div[2]/section[8]"));
        lawyers.addAll(div.findElements(By.cssSelector("div.elementor-column.elementor-col-25.elementor-inner-column")));
        return this.siteUtl.filterLawyersInPage(lawyers, null, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.elementor-flip-box__button")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.xpath("//*[@id=\"content\"]/div/div/section[1]/div[2]/div[2]/div"));
        String text = container.getText();
        if (text.contains("†")) return "Invalid Role";

        String[] split = text.split("\n");
        String name = split[1];
        String role = split[0];

        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Germany",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.cssSelector("div.elementor-element-41d234dd a")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "497613894690" : socials[1]
        );
    }
}

package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Damma extends ByNewPage {

    private final By[] byRoleArray = {
            By.className("elementor-image-box-description")
    };

    public Damma() {
        super(
                "Damma",
                "https://www.damma.com.pe/en/team/",
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
            int[] indexes = {4, 5, 7, 9, 11, 12};

            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"content\"]/div/div/div[3]"));
            List<WebElement> lawyers = div.findElements(By.cssSelector("div.elementor-image-box-content"));

            for (int index : indexes) {
                String xpath = "//*[@id=\"content\"]/div/div/div[" + index + "]";
                div = MyDriver.wait.findElement(By.xpath(xpath));
                lawyers.addAll(div.findElements(By.cssSelector("div.elementor-image-box-content")));
            }

            return lawyers;
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-image-box-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.tagName("body"));
        String[] socials = super.getSocials(container.findElements(By.xpath("//div/div/div/div/div[1]/div[2]/div[1]/div[2]/div/div/a")), false);
        socials[0] = socials[0].replace("https://mail.google.com/mail/?view=cm&ui=2&tf=0&fs=1&to=", "").replace("&su=solicito%20informaci%c3%b3n", "");

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Peru",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "5112223003" : socials[1]
        );
    }
}
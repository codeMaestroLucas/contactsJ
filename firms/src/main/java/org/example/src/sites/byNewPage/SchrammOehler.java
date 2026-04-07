package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class SchrammOehler extends ByNewPage {

    public SchrammOehler() {
        super(
                "Schramm Öhler",
                "https://www.schramm-oehler.at/menschen/",
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
            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"content\"]/div/div[1]/section[3]/div/div/div/div/div/div"));
            return div.findElements(By.cssSelector("div.ecs-link-wrapper[data-href*='https://www.schramm-oehler.at/team/']"));
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".elementor-element-304ce34c")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("elementor-col-66"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String phone = super.getSocialsFromText(container.getText())[1];

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Austria",
                "practice_area", "",
                "email", socials[0],
                "phone", phone.isEmpty() ? "4314097609" : phone
        );
    }
}

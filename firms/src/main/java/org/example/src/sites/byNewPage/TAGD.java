package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class TAGD extends ByNewPage {

    public TAGD() {
        super(
                "TAGD",
                "https://tagdlaw.com.br/en/team-en/",
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
            WebElement div = MyDriver.wait.findElement(By.xpath("/html/body/div[1]/section[4]/div/div/div"));
            List<WebElement> lawyers = div.findElements(By.className("elementor-column"));
            return this.siteUtl.filterLawyersInPage(lawyers, null, false);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("a[href*='https://tagdlaw.com.br/']")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div[1]/section[3]/div/div[1]/div/section/div/div/div"));

        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551130304906" : socials[1]
        );
    }
}

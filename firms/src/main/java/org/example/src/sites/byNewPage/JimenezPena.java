package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class JimenezPena extends ByNewPage {

    public JimenezPena() {
        super(
                "Jiménez Peña",
                "https://jpadvisors.do/en/our-team/",
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

            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"content\"]/div/div[1]/section[3]/div/div/div/div[2]/div/div/div"));
            return div.findElements(By.className("cxps-post-card"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("cxps-title-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("cxps-post-title")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.xpath("/html/body/div[2]/section[1]/div/div/div/div/div/div/div/section[2]/div/div[2]/div"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String practice = extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.className("cx-rst-list")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "the Dominican Republic",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "8099550202" : socials[1]
        );
    }
}

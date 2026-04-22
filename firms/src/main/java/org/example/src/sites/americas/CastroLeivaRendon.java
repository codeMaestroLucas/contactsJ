package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class CastroLeivaRendon extends ByNewPage {

    public CastroLeivaRendon() {
        super(
                "Castro Leiva Rendón",
                "https://castroleiva.com/en/nosotros/#equipo",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.jet-listing-grid__item"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".elementor-element-a10169e h2")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("jet-engine-listing-overlay-link")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".elementor-element-1c3af4b h2")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".elementor-element-a10169e h2")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//*[@id=\"jupiterx-main\"]/div/section[1]/div/div[2]/div/section/div/div/div/div/div/h2/a"));

        String pa = driver.findElement(By.xpath("//*[@id=\"jupiterx-main\"]/div/section[2]/div/div[3]/div/div[2]/div/p")).getAttribute("textContent");
        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Colombia",
                "practice_area", pa,
                "email", socials[0],
                "phone", "576017424641"
        );
    }
}

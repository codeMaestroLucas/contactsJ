package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class NovotnyAdvogados extends ByNewPage {

    public NovotnyAdvogados() {
        super(
                "Novotny Advogados",
                "https://www.novotny.com.br/en/professionals",
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

            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"section_vkfb5rgw4\"]/div"));
            return div.findElements(By.className("advogado-thumb"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("advogado-tittle")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("advogado-tittle")}, "NAME", LawyerExceptions::nameException).replace("->", "").trim();

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.cssSelector("section.content-block[id*='section_'][data-justify='center']"));
        String role = container.getText();
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "552139933600" : socials[1]
        );
    }
}

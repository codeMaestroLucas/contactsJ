package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class EstudioBunge extends ByNewPage {

    public EstudioBunge() {
        super(
                "Estudio Bunge",
                "https://www.estudiobunge.com/nuestros-abogados/?lang=en",
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
        return MyDriver.wait.findElements(By.cssSelector("div.et_pb_column_1_4"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("p")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("et_pb_row_2"));
        String[] socials = super.getSocials(container.findElements(By.className("et_pb_text_inner")), true);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Lawyer",
                "firm", this.name,
                "country", "Argentina",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "541152816000" : socials[1]
        );
    }
}

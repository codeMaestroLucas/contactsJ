package org.example.src.sites.americas;

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
        WebElement container = MyDriver.wait.findElement(By.className("et_pb_row_2"));
        String[] socials = super.getSocialsFromText(container.getText());
        String role = driver.findElement(By.xpath("//div/div/div/div/div[2]/div/div[1]/div/h1")).getText();

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Argentina",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "541152816000" : socials[1]
        );
    }
}

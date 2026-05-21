package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class AntasDaCunhaEcija extends ByNewPage {

    public AntasDaCunhaEcija() {
        super(
                "Antas da Cunha Ecija & Associados",
                "https://adcecija.pt/en/our-team/",
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
        return MyDriver.wait.findElements(By.cssSelector(".equipaFiltroSing"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("bt_bb_headline_content")}, "NAME", LawyerExceptions::nameException);
        String email = extractor.extractLawyerText(lawyer, new By[]{By.className("bt_bb_headline_subheadline")}, "EMAIL", LawyerExceptions::emailException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("bt_bb_column_inner"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("bt_bb_headline_subheadline")}, "ROLE", LawyerExceptions::roleException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Portugal",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.xpath(".//header[contains(., 'PRACTICE AREAS')]/following-sibling::header[1]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", email,
                "phone", "351213420955"
        );
    }
}

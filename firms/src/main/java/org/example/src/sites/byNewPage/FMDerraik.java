package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class FMDerraik extends ByNewPage {

    public FMDerraik() {
        super(
                "FM Derraik",
                "https://www.fmderraik.com.br/equipe/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"equipe\"]/div/div[5]/div"));
            return div.findElements(By.className("card-advogado"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("nome")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("col-lg-4"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("atuacoes-relacionadas")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551130325151" : socials[1]
        );
    }
}

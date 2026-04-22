package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class DBDavidSiBaias extends ByNewPage {

    public DBDavidSiBaias() {
        super(
                "D&B David si Baias",
                "https://www.david-baias.ro/en/our-team/",
                4
        );
    }

    private String currentRole = null;

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String url = null;
        switch (index) {
            case 0:
                url = this.link;
                currentRole = "Partner";
                break;
            case 1:
                url = "https://www.david-baias.ro/en/our-team/counsels/";
                currentRole = "Counsel";
                break;
            case 2:
                url = "https://www.david-baias.ro/en/our-team/managing-associates/";
                currentRole = "Managing Associate";
                break;
            case 3:
                url = "https://www.david-baias.ro/en/our-team/senior-associates/";
                currentRole = "Senior Associate";
                break;
        }
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector(".db23-staff-partners"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("//div/div/div[3]/div[1]/div/div"));
        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", currentRole,
                "firm", this.name,
                "country", "Romania",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "40212253770" : socials[1]
        );
    }
}

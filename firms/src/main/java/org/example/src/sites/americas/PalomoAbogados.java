package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PalomoAbogados extends ByPage {

    public PalomoAbogados() {
        super(
                "Palomo Abogados",
                "https://palomoabogadosen.squarespace.com",
                9
        );
    }

    String[] links = {
            "/federico-palomo-1",
            "/hector-palomo-1",
            "/sandra-iriarte-1",
            "/cynthia-guillioli-1",
            "/monica-rodriguez-1",
            "/paula-passarelli-1",
            "/flor-mendez-1",
            "/miguel-colop",
            "/ingrid-gatica-1"
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link + links[index]);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("main.Main.Main--page"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String rawText = lawyer.getAttribute("innerText");
        String[] socials = this.getSocialsFromText(rawText);
        String name = lawyer.findElement(By.xpath("//div/div/h1/strong")).getText();

        return Map.of(
                "link", Objects.requireNonNull(this.driver.getCurrentUrl()),
                "name", name,
                "role", "-----",
                "firm", this.name,
                "country", "Guatemala",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "50222797474" : socials[1]
        );
    }
}

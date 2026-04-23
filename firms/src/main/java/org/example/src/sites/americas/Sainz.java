package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class Sainz extends ByNewPage {

    public Sainz() {
        super(
                "Sainz",
                "https://www.sainzmx.com/socios",
                2
        );
    }

    private String currentRole =  "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.sainzmx.com/consejer0s";
        String url = index == 0 ? this.link : otherUrl;
        currentRole = index == 0 ? "Partner" : "Counsel";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("li.list-item"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[href*='/']")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h2")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("sqsrte-small"));
        String[] outerHTMLS = container.getAttribute("innerText").split("\n");
        String email = outerHTMLS[0];
        String phone = outerHTMLS[1];


        return Map.of(
                "link", link,
                "name", name,
                "role", currentRole,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "525591785059" : phone
        );
    }
}

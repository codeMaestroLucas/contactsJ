package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CannizzoOrtizAsociados extends ByNewPage {

    public CannizzoOrtizAsociados() {
        super(
                "Cannizzo, Ortiz y Asociados",
                "https://cannizzo.com.mx/en/buscar/",
                26
        );
    }

    char[] alphabet = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();

        WebElement filter = driver.findElement(By.className("menuletras"));
        String string = "a[data-letra=" + alphabet[index] + "]";
        MyDriver.clickOnElement(filter.findElement(By.cssSelector(string)));
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.className("abogado-line"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a[href*='https://cannizzo.com.mx/en/']")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("nameAbogado")}, "NAME", LawyerExceptions::nameException);
        
        String practiceArea = lawyer.findElements(By.cssSelector("ul li a")).stream()
                .map(WebElement::getText)
                .collect(Collectors.joining(", "));

        String[] socials = this.getSocials(lawyer.findElements(By.tagName("a")), false);

        String link = this.openNewTab(lawyer);
        
        WebElement container = MyDriver.wait.findElement(By.xpath("/html/body/div[1]/div/div/div/div/div/div/div[1]/div/div/div[3]/div/div"));

        String role = container.getText();
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "525552795980" : socials[1]
        );
    }
}

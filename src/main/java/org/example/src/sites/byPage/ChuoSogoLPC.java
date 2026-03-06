package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ChuoSogoLPC extends ByPage {

    public ChuoSogoLPC() {
        super(
                "Chuo Sogo LPC",
                "https://www.clo.jp/english/lawyers/",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"main\"]/section/div/div/div[2]/ul"))
            );

            List<WebElement> lawyers = div.findElements(By.cssSelector("li"));

            div = driver.findElement(By.xpath("//*[@id=\"main\"]/section/div/div/div[3]/ul"));
            lawyers.addAll(div.findElements(By.cssSelector("li")));

            div = driver.findElement(By.xpath("//*[@id=\"main\"]/section/div/div/div[4]/ul"));
            lawyers.addAll(div.findElements(By.cssSelector("li")));

            div = driver.findElement(By.xpath("//*[@id=\"main\"]/section/div/div/div[5]/ul"));
            lawyers.addAll(div.findElements(By.cssSelector("li")));

            return lawyers;
        } catch (Exception e) {
            return this.driver.findElements(By.cssSelector("ul li a[href*='/lawyers/']"));
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, new By[]{By.tagName("a")}, "NAME", LawyerExceptions::nameException);
    }

    private String constructEmail(String name) {
        name = TreatLawyerParams.treatNameForEmail(name);
        String[] parts = name.split("\\s+");
        if (parts.length < 2) return "";

        String lastName = parts[parts.length - 1];
        String firstNameLetter = parts[0].substring(0, 1);
        return lastName + "_" + firstNameLetter + "@clo.gr.jp";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", "---",
                "firm", this.name,
                "country", "Japan",
                "practice_area", "",
                "email", constructEmail(name),
                "phone", "81666768834"
        );
    }
}

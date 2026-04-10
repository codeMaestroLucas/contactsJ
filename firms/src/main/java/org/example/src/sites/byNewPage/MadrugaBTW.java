package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MadrugaBTW extends ByNewPage {

    public MadrugaBTW() {
        super(
                "Madruga BTW",
                "https://madruga.com/en/team/",
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
        int[] indexes = {4, 5, 6, 7};
        String xpath = "";
        try {
            WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"post-3901\"]/div/div/div[3]/div"));
            List<WebElement> lawyers = div.findElements(By.cssSelector("h2 > a[href*='https://madruga.com/en/']"));

            for (int index : indexes) {
                xpath = "//*[@id=\"post-3901\"]/div/div/div[" + index + "]/div";
                div = driver.findElement(By.xpath(xpath));
                lawyers.addAll(div.findElements(By.cssSelector("h2 > a[href*='https://madruga.com/en/']")));
            }

            return lawyers;
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
        this.openNewTab(lawyer);

        String name = extractor.extractLawyerAttribute(driver.findElement(By.tagName("body")), new By[]{By.xpath("//div/div/div[2]/div/div[2]/div/div[1]/div/div[1]/div/div[1]/div/h2")}, "NAME", "textContent", LawyerExceptions::nameException);
        String email = MyDriver.wait.findElement(By.xpath("//div/div/div[2]/div/div[1]/div[4]/div[2]/div/div/a")).getAttribute("href");

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", email,
                "phone", "551130450520"
        );
    }
}

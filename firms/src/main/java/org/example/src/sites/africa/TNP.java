package org.example.src.sites.africa;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TNP extends ByNewPage {

    public TNP() {
        super(
                "TNP",
                "https://tnp.com.ng/team",
                25
        );
    }

    final char[] alphabet = {
            'b','c','d','e','f','g','h','i','j','k','l','m',
            'n','o','p','q','r','s','t','u','v','w','x','y','z'
    };


    @Override
    protected void accessPage(int index) throws InterruptedException {
        if  (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        }

        WebElement input = driver.findElement(By.xpath("//*[@id=\"content\"]/div/div/div[3]/div/div[3]/div/div/div/div/input"));
        input.clear();
        input.sendKeys(String.valueOf(alphabet[index]));
        Thread.sleep(200);
        input.sendKeys(Keys.ENTER);
        Thread.sleep(800);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));

            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"content\"]/div/div/div[4]/div/div/div/div")));
            List<WebElement> lawyers = div.findElements(By.cssSelector("a[href*='https://tnp.com.ng/team/']"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("h2.elementor-heading-title")}, false);
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
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-page-title")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".elementor-element-7b22efc h2")}, "ROLE", LawyerExceptions::roleException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div[2]"));

        String email = extractor.extractLawyerText(container, new By[]{By.className("jeg_module_1865__69bf8672340a3")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("elementor-element-4f4f061")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", email,
                "phone", "23414537121"
        );
    }
}

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

public class MomoOMatsuoAndNamba extends ByPage {

    public MomoOMatsuoAndNamba() {
        super(
                "Momo-o Matsuo And Namba",
                "https://www.mmn-law.gr.jp/en/lawyers/index.html",
                1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            By by = By.className("p-lawyers-index__item");

            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement partnerDiv = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"wrapper\"]/main/div[1]/div/div/div/div[2]/section[1]/div")
                    )
            );
            List<WebElement> lawyers = partnerDiv.findElements(by);

            WebElement counselsDiv = driver.findElement(By.xpath("//*[@id=\"wrapper\"]/main/div[1]/div/div/div/div[2]/section[2]"));
            lawyers.addAll(counselsDiv.findElements(by));

            counselsDiv = driver.findElement(By.xpath("//*[@id=\"wrapper\"]/main/div[1]/div/div/div/div[2]/section[3]"));
            lawyers.addAll(counselsDiv.findElements(by));

            WebElement advisorDiv = driver.findElement(By.xpath("//*[@id=\"wrapper\"]/main/div[1]/div/div/div/div[2]/section[5]"));
            lawyers.addAll(advisorDiv.findElements(by));

            return lawyers;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("main")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String constructEmail(String name) {
        String[] nameParts = TreatLawyerParams.treatNameForEmail(name).split(" ");
        return nameParts[0] + "." + nameParts[nameParts.length - 1] + "@mmn-law.gr.jp";
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);

        return Map.of(
                "link", this.link,
                "name", name,
                "role", "---",
                "firm", this.name,
                "country", "Japan",
                "practice_area", "",
                "email", this.constructEmail(name),
                "phone", "81332882080"
        );
    }
}
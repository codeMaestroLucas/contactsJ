package org.example.src.sites._standingBy.otherIsuues;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LBCA extends ByNewPage {
    private final By[] byRoleArray = {
            By.cssSelector("h6")
    };

    public LBCA() {
        super(
            "LBCA",
            "https://lbca.com.br/equipe/",
            53
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) return;

        MyDriver.rollDown(1, 1);

        WebElement nextBtn = driver
                .findElement(By.className("jet-filters-pagination"))
                .findElement(By.cssSelector("div.jet-filters-pagination__item.prev-next.next"));

        Actions actions = new Actions(driver);
        actions.moveToElement(nextBtn).perform();

        // 3. Small pause (300ms) like your JS code
        Thread.sleep(300);

        // 4. Click the button
        nextBtn.click();



        Thread.sleep(2000L);
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "sócio (a)",
                "diretor (a)",
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            WebElement lawyersDiv = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"filtro-prof\"]/div/div")
                    )
            );
            List<WebElement> lawyers = lawyersDiv.findElements(By.className("jet-listing-grid__item"));
//            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
            return null;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("jet-engine-listing-overlay-link")
        };
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);

        MyDriver.openNewTab(link);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("h2")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("/html/body/main/div[2]/div[2]/div[9]/div/div/div[1]"),
                By.cssSelector("h6")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }


    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("/html/body/main/div[2]/div[2]/div[2]/ul/li/span[2]")
        };
        return extractor.extractLawyerText(lawyer, byArray, "EMAIL", LawyerExceptions::emailException);
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.xpath("/html/body/main/div[2]/div[2]"));

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", "",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", this.getPracticeArea(div),
                "email", this.getEmail(div),
                "phone",  "551121495400"
        );
    }
}

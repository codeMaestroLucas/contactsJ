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

public class Pulegal extends ByNewPage {
    String[] validRoles = new String[]{
            "partner",
            "counsel",
            "director"
    };

    public Pulegal() {
        super(
            "Pulegal",
            "https://ppulegal.com/en/team/",
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[href^='https://ppulegal.com/en/bd-abogados/']")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("//*[@id=\"page\"]/div/section[1]/div/div[1]/div/div[2]/div")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText().replace("\n", " ");
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("//*[@id=\"page\"]/div/section[1]/div/div[1]/div/div[3]")
        };
        String role = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("//*[@id=\"page\"]/div/section[1]/div/div[1]/div/div[6]/div/div[2]/div/ul/li[2]/span[2]"),
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "textContent", LawyerExceptions::countryException);
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("//*[@id=\"page\"]/div/section[1]/div/div[1]/div/div[4]/div/p")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String[] split = extractor.extractLawyerAttribute(lawyer, byArray, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException).split("/");
        return split[0];
    }


    private String[] getSocials(WebElement lawyer) {
        String email = ""; String phone = "";

        WebElement socialsDiv = lawyer.findElement(By.xpath("//*[@id=\"page\"]/div/section[1]/div//div[6]/div"));
        email = socialsDiv.findElement(By.cssSelector("a[href^='mailto']")).getAttribute("href");
        phone = socialsDiv.findElement(By.xpath("//*[@id=\"page\"]/div/section[1]/div/div[1]/div/div[6]/div/div[2]/div/ul/li[1]/span[2]")).getText();

        return new String[]{ email, phone };
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.xpath("//*[@id=\"page\"]/div/section[1]/div/div[1]"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}

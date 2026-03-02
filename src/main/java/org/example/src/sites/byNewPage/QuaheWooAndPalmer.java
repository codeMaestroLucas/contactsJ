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

public class QuaheWooAndPalmer extends ByNewPage {

    public QuaheWooAndPalmer() {
        super(
                "Quahe Woo & Palmer",
                "https://www.qwp.sg/our-lawyers/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("ourteam")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".ourteam_more a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    public String getName() {
        return driver.findElement(By.xpath("//div/div[3]/div/div/div/h2[1]")).getAttribute("textContent");
    }

    private String getRole()  {
        return driver.findElement(By.xpath("//div/div[3]/div/div/div/div[4]/div/h2/em/span\n")).getAttribute("textContent");
    }

    private String[] getSocials() {
        try {
            String phone = driver.findElement(By.xpath("//div/div[3]/div/div/div/div[5]/div[5]/div[2]")).getAttribute("textContent");
            String email = driver.findElement(By.xpath("//div/div[3]/div/div/div/div[5]/div[5]/div[3]")).getAttribute("textContent");
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", "6566220366"};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(),
                "role", this.getRole(),
                "firm", this.name,
                "country", "Singapore",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "6566220366" : socials[1]
        );
    }
}

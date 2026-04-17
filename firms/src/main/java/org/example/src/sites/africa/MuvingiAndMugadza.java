package org.example.src.sites.africa;

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

public class MuvingiAndMugadza extends ByNewPage {

    public MuvingiAndMugadza() {
        super(
                "Muvingi & Mugadza",
                "https://www.mmmlawfirm.co.zw/people/",
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

            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"post-2123\"]/div/div/div/div[2]")));
            List<WebElement> lawyers = div.findElements(By.className("et_pb_blurb_content"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("et_pb_blurb_description")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("et_pb_module_header")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("et_pb_blurb_description")}, "ROLE", "textContent", LawyerExceptions::roleException);

        this.openNewTab(lawyer);

        MyDriver.clickOnAddBtn(By.id("cookie_action_close_header"));

        String email = "";
        try {
            email = driver.findElement(By.xpath("//div/div/div/div[1]/div/div[2]/div[1]/div/div[2]/h6")).getAttribute("textContent");
        } catch (Exception e) {}

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Zimbabwe",
                "practice_area", role,
                "email", email,
                "phone", "2630242798214"
        );
    }
}
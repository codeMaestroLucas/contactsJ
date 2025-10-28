package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class FlichyGrange extends ByPage {

    public FlichyGrange() {
        super(
                "FLICHY GRANGÉ",
                "https://www.flichygrange.com/our-partners.htm",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnAddBtn(By.cssSelector("button.bandeauCookies__btn.focus"));
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("ficheavocat")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.ficheHeader > a.moreinfo")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::nameException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.ficheHeader > a.moreinfo > h3")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        List<WebElement> socials = lawyer
                .findElement(By.className("ficheFooter"))
                .findElements(By.tagName("a"));
        return super.getSocials(socials, false);
    }

    public Object getLawyer(WebElement lawyer) throws Exception {

        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "Partner",
                "firm", this.name,
                "country", "France",
                "practice_area", "",
                "email", socials[0],
                "phone", "330156623000"
        );
    }
}
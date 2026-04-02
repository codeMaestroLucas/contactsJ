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

public class Anli extends ByPage {

    public Anli() {
        super(
                "Anli",
                "https://www.anlilaw.com/100043/",
                15
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.anlilaw.com/100043/pn" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("ul.ul1 > li.l1")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".tit span")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    public String[] getSocials(String name) {
        String[] parts = TreatLawyerParams.treatNameForEmail(name).split(" ");
        String email = parts[parts.length -1] + parts[0] + "@anlilaw.com";
        return new String[] {email, "861085879199"};
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("tit")}, "NAME", "textContent", LawyerExceptions::nameException).split("Partner")[0].trim();
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".tit span")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String practice = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("info")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);

        String[] socials = this.getSocials(name);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the UAE",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1]
        );
    }
}

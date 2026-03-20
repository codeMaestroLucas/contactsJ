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

public class GladeMichelWirtz extends ByNewPage {

    public GladeMichelWirtz() {
        super(
                "Glade Michel Wirtz",
                "https://www.glademichelwirtz.com/en/team/?position=partner#anwaelte",
                2
        );
    }

    private String currentRole = "";


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.glademichelwirtz.com/en/team/?position=counsel#anwaelte";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();

        currentRole = index == 0 ? "Partner" : "Counsel";

    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-box")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='https://www.glademichelwirtz.com/en/team/']")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            return super.getSocialsFromText(lawyer.getText());

        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("person-name")}, "NAME", LawyerExceptions::nameException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.xpath("/html/body/div[2]/div/div[2]/div/p[3]"));
        String[] socials = this.getSocials(container);


        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", currentRole,
                "firm", this.name,
                "country", "Germany",
                "practice_area", "",
                "email", socials[0].replace("(at)", "@"),
                "phone", socials[1].isEmpty() ? "4921120052320" : socials[1]
        );
    }
}

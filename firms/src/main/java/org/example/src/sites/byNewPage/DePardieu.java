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

public class DePardieu extends ByNewPage {

    public DePardieu() {
        super(
                "De Pardieu",
                "https://www.de-pardieu.com/en/lawyers-team/",
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
            WebElement until = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"page-lawyers\"]/div[3]/div")));

            List<WebElement> lawyers = until.findElements(By.cssSelector("div.col-12"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("card-lawyer__status")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.tagName("a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("banner-lawyer__title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("banner-lawyer__status")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = {By.className("banner-lawyer__expertises")};
            return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            WebElement social = lawyer.findElement(By.className("intro__infos"));
            String[] socialsFromText = super.getSocialsFromText(social.getText());
            socialsFromText[0] = social.findElement(By.cssSelector("div.intro__infos-item.--email > a")).getAttribute("href");
            return socialsFromText;
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.id("page-lawyer"));

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", link,
                "name", this.getName(container),
                "role", this.getRole(container),
                "firm", this.name,
                "country", "France",
                "practice_area", this.getPracticeArea(container),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "33153577170" : socials[1]
        );
    }
}

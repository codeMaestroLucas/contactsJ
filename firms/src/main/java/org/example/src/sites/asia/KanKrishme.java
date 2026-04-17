package org.example.src.sites.asia;

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

public class KanKrishme extends ByNewPage {

    public KanKrishme() {
        super(
                "Kan & Krishme",
                "https://kankrishme.com/our-team/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("wpsm_single_team")));
            List<WebElement> wpsmTeam1BDesig = this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("wpsm_team_1_b_desig")}, true);
            wpsmTeam1BDesig.removeFirst();
            return wpsmTeam1BDesig;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("p.wpsm_team_1_b_desc a")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement wrap = driver.findElement(By.className("vc_column-inner"));
        String[] socials = super.getSocials(wrap.findElements(By.tagName("a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(wrap, new By[]{By.className("ct-team-title")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(wrap, new By[]{By.className("ct-team-position")}, "ROLE", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "India",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "911143776666" : socials[1]
        );
    }
}

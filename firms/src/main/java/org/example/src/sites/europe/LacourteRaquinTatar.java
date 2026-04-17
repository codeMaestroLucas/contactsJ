package org.example.src.sites.europe;

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

public class LacourteRaquinTatar extends ByNewPage {

    public LacourteRaquinTatar() {
        super(
                "Lacourte Raquin Tatar",
                "https://lacourte.com/en/our-team",
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
            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"anchor-54\"]/div/ul"))
            );

            List<WebElement> lawyers = div.findElements(By.cssSelector("a[href*='/en/team-members/']"));

            div = driver.findElement(By.xpath("//*[@id=\"anchor-58\"]/div/ul"));
            lawyers.addAll(div.findElements(By.cssSelector("a[href*='/en/team-members/']")));

            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileUrl = lawyer.getAttribute("href");
        MyDriver.openNewTab(profileUrl);
        return profileUrl;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement content = driver.findElement(By.cssSelector(".c-PersonIntro_Infos"));

        String name = extractor.extractLawyerAttribute(content, new By[]{By.cssSelector(".o-H2")}, "NAME", "textContent", LawyerExceptions::nameException).split(" is a ")[0];
        String role = extractor.extractLawyerAttribute(content, new By[]{By.cssSelector(".o-H2")}, "ROLE", "textContent", LawyerExceptions::roleException).split(" Partner")[0] + " Partner";
        String[] socials = super.getSocials(content.findElements(By.cssSelector("ul.u-block > li > a")), false);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "France",
                "practice_area", "Real Estate",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "33158544000" : socials[1]
        );
    }
}

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

public class Granrut extends ByNewPage {

    public Granrut() {
        super(
                "Granrut",
                "https://www.herald-avocats.com/en/categorie/partners/",
                3
        );
    }

    private final String[] otherLinks = {
        "",
        "https://www.herald-avocats.com/en/categorie/counsels/",
        "https://www.herald-avocats.com/en/categorie/of-counsel/"
    };

    private String currentRole = "";

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = otherLinks[index];
        String url = index == 0 ? this.link : otherUrl;
        currentRole = index == 0 ? "Partner" : "Counsel";
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("avocat-item")));
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

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);

        this.openNewTab(lawyer);
        WebElement bio = driver.findElement(By.className("avocats-informations"));

        String[] socials = super.getSocials(bio.findElements(By.tagName("li")), true);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", currentRole,
                "firm", this.name,
                "country", "France",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "330153431515" : socials[1]
        );
    }
}

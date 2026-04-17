package org.example.src.sites.asia;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SOIP extends ByNewPage {

    private final List<Map<String, String>> preCollected = new ArrayList<>();
    private int lawyerIdx = 0;

    public SOIP() {
        super(
                "S&O IP",
                "https://www.so-ipr.com/our-team",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        preCollected.clear();
        lawyerIdx = 0;
    }

    /** Extracts name, role, and link from the currently visible tab's team-item elements. */
    private void collectFromCurrentTab() {
        List<WebElement> lawyers = driver.findElements(By.className("team-item"));
        for (WebElement l : this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("desc")}, true)) {
            try {
                String name = l.findElement(By.className("title")).getText();
                String role = l.findElement(By.className("desc")).getText();
                String link = l.findElement(By.tagName("a")).getAttribute("href");
                if (link != null && !link.isEmpty()) {
                    preCollected.add(Map.of("name", name, "role", role, "link", link));
                }
            } catch (Exception ignored) {}
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-item")));
            collectFromCurrentTab();

            MyDriver.clickOnElement(By.xpath("/html/body/div/article/section[3]/div/div[1]/div[2]/ul/li[3]"));
            Thread.sleep(1500);
            collectFromCurrentTab();

            MyDriver.clickOnElement(By.xpath("/html/body/div/article/section[3]/div/div[1]/div[2]/ul/li[2]"));
            Thread.sleep(1500);
            collectFromCurrentTab();

            // Return a placeholder list matching preCollected size.
            // getLawyer uses preCollected by index — the WebElement is not accessed.
            return Collections.nCopies(preCollected.size(), null);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        // Navigation is handled directly in getLawyer using preCollected links.
        return "";
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        Map<String, String> data = preCollected.get(lawyerIdx++);

        String link = data.get("link");
        String name = data.get("name");
        String role = data.get("role");

        MyDriver.openNewTab(link);

        WebElement container = driver.findElement(By.className("contact"));
        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Vietnam",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "842873009678" : socials[1]
        );
    }
}

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

public class BossYoung extends ByNewPage {

    public BossYoung() {
        super(
                "Boss & Young",
                "https://www.boss-young.com/teamList",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    private final String[] validRoles = {
            "合伙人", "高级合伙人", "管理合伙人", "首席合伙人", "创始合伙人", "顾问", "高级顾问", "律师", "高级律师", "主任", "主席"
    };

    private static final Map<String, String> ROLE_MAP = Map.ofEntries(
            Map.entry("合伙人",   "Partner"),
            Map.entry("高级合伙人", "Senior Partner"),
            Map.entry("管理合伙人", "Managing Partner"),
            Map.entry("首席合伙人", "Partner"),
            Map.entry("创始合伙人", "Partner"),
            Map.entry("顾问",    "Counsel"),
            Map.entry("高级顾问",  "Senior Counsel"),
            Map.entry("高级律师",  "Senior Associate"),
            Map.entry("主任",    "Partner"),
            Map.entry("主席",    "Partner")
    );

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("team-box")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("post")}, true, validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("typeBox"));

        String role = this.getRole();
        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", "",
                "role", role,
                "firm", this.name,
                "country", "China",
                "practice_area", "",
                "email", driver.findElement(By.xpath("//div/div/div/div[1]/div/div[2]/div[2]/div/div[2]/span[2]")).getAttribute("textContent"),
                "phone", extractor.extractLawyerText(container, new By[] {By.className("phoneLine")}, "PHONE", LawyerExceptions::phoneException)
        );
    }

    private String getRole()  {
        String raw =  driver.findElement(By.xpath("//*[@id=\"__layout\"]/div/div/div/div[1]/div/div[2]/div[1]/div[1]/div[2]/span")).getAttribute("textContent");

        if (raw == null) return "";

        for (Map.Entry<String, String> entry : ROLE_MAP.entrySet()) {
            if (raw.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "";
    }
}

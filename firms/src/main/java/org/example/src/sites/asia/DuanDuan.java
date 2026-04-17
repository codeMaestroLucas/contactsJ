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

public class DuanDuan extends ByNewPage {

    public DuanDuan() {
        super(
                "Duan & Duan",
                "https://www.duanduan.com/team",
                96
        );
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
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.duanduan.com/team?page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("lawyerBox")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("field")}, true, validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.xpath(".")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("field")}, "ROLE", LawyerExceptions::roleException).replace("职务：", "").trim();

        if (role == null) return "";

        for (Map.Entry<String, String> entry : ROLE_MAP.entrySet()) {
            if (role.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "";
    }


    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String role = this.getRole(lawyer);

        String link = this.openNewTab(lawyer);

        WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
        WebElement container = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"__layout\"]/div/div[1]/div/div[2]/div/div[2]/div[2]")));

        String[] socials = super.getSocialsFromText(container.getText());
        return Map.of(
                "link", link,
                "name", "----",
                "role", role,
                "firm", this.name,
                "country", "China",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "862162191103" : socials[1]
        );
    }
}

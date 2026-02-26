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

public class CapitalEquityLegalGroup extends ByNewPage {

    public CapitalEquityLegalGroup() {
        super(
                "Capital Equity Legal Group",
                "https://en.celg.cn/product_1562.html?keyword=&area=&member_sortid_2=",
                80
        );
    }

    String[] validRoles = {
            "合伙人", "高级合伙人", "管理合伙人", "首席合伙人", "创始合伙人", "顾问", "高级顾问", "律师", "高级律师", "主任", "主席"
    };

    private final By[] byRoleArray = {By.className("intro")};



    private static final Map<String, String> ROLE_MAP = Map.ofEntries(
            Map.entry("合伙人",   "Partner"),
            Map.entry("高级合伙人", "Senior Partner"),
            Map.entry("管理合伙人", "Managing Partner"),
            Map.entry("首席合伙人", "Partner"),       // Chief Partner → Partner
            Map.entry("创始合伙人", "Partner"),       // Founding Partner → Partner
            Map.entry("顾问",    "Counsel"),
            Map.entry("高级顾问",  "Senior Counsel"),
            Map.entry("高级律师",  "Senior Associate"),
            Map.entry("主任",    "Partner"),          // Director-level
            Map.entry("主席",    "Partner")           // Chairman-level
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://en.celg.cn/product.php?gopage=" + (index + 1) + "&page_id=1562&keyword=&area=&member_sortid_2=";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item"))
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h2")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("p")};
        String raw = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);


        // procura o primeiro match no mapa (mais específico primeiro)
        for (Map.Entry<String, String> entry : ROLE_MAP.entrySet()) {
            if (raw.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String text = lawyer.findElement(By.className("lawyer-intro-desc")).getText();
            return super.getSocialsFromText(text);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("lawyer-intro"));
        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "China",
                "practice_area", "",
                "email", socials[0].split("：")[1],
                "phone", socials[1]
        );
    }
}
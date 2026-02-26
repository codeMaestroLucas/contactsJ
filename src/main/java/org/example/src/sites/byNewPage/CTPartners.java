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

// A lot of missing info in server side. Ignore errors
public class CTPartners extends ByNewPage {

    public CTPartners() {
        super(
                "C&T Partners",
                "http://www.ct-partners.com.cn/PC/#/goods",
                5
        );
    }

    String[] validRoles = {
            "合伙人", "高级合伙人", "管理合伙人", "首席合伙人", "创始合伙人", "顾问", "高级顾问", "律师", "高级律师", "主任", "主席"
    };

    private final By[] byRoleArray = {By.cssSelector(".info p:nth-child(2)")};

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
        if (index == 0) {
            this.driver.get(this.link);
        } else {
            MyDriver.clickOnElement(By.className("btn-next"));
            Thread.sleep(2000L);
        }
        MyDriver.waitForPageToLoad();
        Thread.sleep(2000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("li.one"))
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("li")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("li:nth-child(2)")};
        String raw = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);

        // procura o primeiro match no mapa (mais específico primeiro)
        for (Map.Entry<String, String> entry : ROLE_MAP.entrySet()) {
            if (raw.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    private String[] getEmail(WebElement lawyer) {
        try {
            List<WebElement> lis = lawyer.findElements(By.tagName("li"));
            String[] socials = super.getSocials(lis, true);
            try {
              socials[0] = socials[0].split("[:：]")[1];
            } catch (Exception e) {}

            return socials;
        } catch (Exception e) {
            return new String[] {"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        Thread.sleep(2000L); // The DOM doesn't load correctly when oppening a new page.
        WebElement div = driver.findElement(By.cssSelector("div.content_div"));

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "China",
                "practice_area", "",
                "email", this.getEmail(div)[0],
                "phone", "862586633108"
        );
    }
}
package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShandongDehengLaw extends ByPage {

    private final By[] byRoleArray = {
            By.cssSelector("span:nth-of-type(1)")
    };

    public ShandongDehengLaw() {
        super(
                "Shandong Deheng Law",
                "https://www.deheng.com/lawyer/",
                47
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.deheng.com/lawyer/index_" + (index + 1) + ".html";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    //! CHINESE ROLES
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("li-lawyer"))
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
        return link != null ? link : "";
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String raw = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);

        if (raw == null) return "";

        for (Map.Entry<String, String> entry : ROLE_MAP.entrySet()) {
            if (raw.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "";
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("i")};
        String location = extractor.extractLawyerText(lawyer, byArray, "LOCATION", LawyerExceptions::countryException);

        if (location == null) return "China";

        if (location.toLowerCase().contains("悉尼") || location.toLowerCase().contains("sydney")) {
            return "Australia";
        }
        return "China";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        By[] byEmail = {By.tagName("p")};
        String email = extractor.extractLawyerText(lawyer, byEmail, "EMAIL", LawyerExceptions::emailException);

        Map<String, String> lawyerData = new HashMap<>();
        lawyerData.put("link", this.getLink(lawyer));
        lawyerData.put("name", "");
        lawyerData.put("role", this.getRole(lawyer));
        lawyerData.put("firm", this.name);
        lawyerData.put("country", this.getCountry(lawyer));
        lawyerData.put("practice_area", "");
        lawyerData.put("email", email != null ? email : "");
        lawyerData.put("phone", "8601065210161");

        return lawyerData;
    }
}
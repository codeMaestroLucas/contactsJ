package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class UshijimaAndPartners extends ByPage {

    public UshijimaAndPartners() {
        super(
                "Ushijima & Partners",
                "https://www.ushijima-law.gr.jp/en/attorney/",
                4
        );
    }

    private final By[] byRoleArray = {
            By.className("title")
    };


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.ushijima-law.gr.jp/en/attorney/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "senior associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("p-attorney-list__item")));

            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("ruby")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String constructEmail(String name) {
        name = TreatLawyerParams.treatNameForEmail(name);
        String[] parts = name.split("\\s+");
        if (parts.length < 2) return "";

        return parts[0] + "." + parts[parts.length - 1] + "@ushijima-law.gr.jp";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Japan",
                "practice_area", "",
                "email", constructEmail(name),
                "phone", "81332240707"
        );
    }
}
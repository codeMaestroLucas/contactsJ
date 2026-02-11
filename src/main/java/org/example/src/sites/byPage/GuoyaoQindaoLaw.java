package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class GuoyaoQindaoLaw extends ByPage {

    private final By[] byRoleArray = {
            By.className("post")
    };

    public GuoyaoQindaoLaw() {
        super(
                "Guoyao Qindao Law",
                "https://en.guoyaoqindao.com/professional/index.html?leeter_active=ALL&work_active=&major_active=&keyword=&username=",
                18
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        }  else {
            MyDriver.clickOnElement(By.className("layui-laypage-next"));
            Thread.sleep(2000);
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"director", "partner", "counsel"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("listsCon"))
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{}, "LINK", "href", LawyerExceptions::linkException);
        return link.isEmpty() ? this.link : link;
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("three")};
        String text = extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        return text.replace("Specialized Areas：", "").trim();
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> spanSocials = lawyer.findElements(By.cssSelector(".two1 span"));
            String phone = spanSocials.get(0).getText().trim();
            String email = spanSocials.get(1).getText().trim();
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", "", // name in Chinese, better build it latter
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "China",
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "53158681777" : socials[1]
        );
    }
}

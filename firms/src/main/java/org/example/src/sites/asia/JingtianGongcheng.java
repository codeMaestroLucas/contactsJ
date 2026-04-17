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

public class JingtianGongcheng extends ByNewPage {

    public JingtianGongcheng() {
        super(
                "Jingtian & Gongcheng",
                "https://www.jingtian.com/en/zyry/?text=&zyly=&bgdd=&key=",
                23
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.jingtian.com/en/zyry/?text=&zyly=&bgdd=&key=&Page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("ul.zyry_ul.cf > li.li")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("div.zyry_name.hidden")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".zyry_name span")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("zyry_name")}, "ROLE", LawyerExceptions::roleException).replace(name, "").trim();
        String practice = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("li.zyry_intro_li.cf")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("zyry_intro"));
        String[] socials = super.getSocials(container.findElements(By.tagName("li")), true);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "China",
                "practice_area", practice.replace("Practice:", ""),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "861058091088" : socials[1]
        );
    }
}

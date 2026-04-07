package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PSTBN extends ByNewPage {

    public PSTBN() {
        super(
                "PSTBN",
                "https://www.pstbn.com.py/en/abogados-ingles/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector(".elementor-widget-image a"));
            if (lawyers.size() > 3) {
                return lawyers.stream().skip(3).collect(Collectors.toList());
            }
            return lawyers;
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
        String link = this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("elementor-widget-wrap"));
        String name = extractor.extractLawyerText(container, new By[]{By.className("elementor-element-cdaea19")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(container, new By[]{By.className("elementor-element-69b086a")}, "ROLE", LawyerExceptions::roleException);

        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);
        String practice = extractor.extractLawyerText(container, new By[]{By.className("elementor-element-4604b11")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException).replace("Practice Areas:", "").trim();

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Paraguay",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "595213199000" : socials[1]
        );
    }
}

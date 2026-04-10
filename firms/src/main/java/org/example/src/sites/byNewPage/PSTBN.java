package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

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
            // Removing the first 5
            lawyers.removeFirst();lawyers.removeFirst();lawyers.removeFirst();lawyers.removeFirst();lawyers.removeFirst();
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

        WebElement container = driver.findElement(By.xpath("/html/body/div[2]/section[1]/div/div[2]/div/section[1]/div/div/div"));
        String name = extractor.extractLawyerText(container, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException);
        String role = container.getText();
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String[] socials = super.getSocialsFromText(role);
        socials[0] = container.findElement(By.tagName("a")).getAttribute("href");

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Paraguay",
                "practice_area", role,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "595213199000" : socials[1]
        );
    }
}

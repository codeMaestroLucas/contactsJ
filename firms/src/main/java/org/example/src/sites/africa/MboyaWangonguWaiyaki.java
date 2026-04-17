package org.example.src.sites.africa;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class MboyaWangonguWaiyaki extends ByNewPage {

    public MboyaWangonguWaiyaki() {
        super(
                "Mboya Wangongu & Waiyaki Advocates",
                "https://lexgroupafrica.com/our-partners/",
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
        return MyDriver.wait.findElements(By.cssSelector("a.elementor-button.elementor-button-link.elementor-size-sm[href*='https://lexgroupafrica.com/']"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("//*[@id=\"content\"]/div/div[1]/div[2]/div/div[2]"));
        String role = container.getText();
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String name = extractor.extractLawyerText(container, new By[]{By.cssSelector("h4.elementor-heading-title")}, "NAME", LawyerExceptions::nameException);

        String[] socials = super.getSocialsFromText(role);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Kenya",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("elementor-element-a50ea63")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "254202164424" : socials[1]
        );
    }
}

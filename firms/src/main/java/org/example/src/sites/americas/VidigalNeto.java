package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class VidigalNeto extends ByNewPage {

    public VidigalNeto() {
        super(
                "Vidigal Neto Advogados",
                "https://en.vidigalneto.com.br/professionals",
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
        return MyDriver.wait.findElements(By.cssSelector("div.post-content.equipe a[href*='https://en.vidigalneto.com.br/professionals/']"));
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
        WebElement container = driver.findElement(By.tagName("body"));

        String role = extractor.extractLawyerAttribute(container, new By[]{By.xpath("//div/div[3]/div/h2[2]")}, "ROLE", "textContent", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String name = extractor.extractLawyerAttribute(container, new By[]{By.xpath("//div/div[3]/div/h2[1]")}, "NAME", "textContent", LawyerExceptions::nameException);
        String[] socials = super.getSocialsFromText(extractor.extractLawyerAttribute(container, new By[]{By.className("contacto")}, "CONTACT", "textContent", LawyerExceptions::socialsException));

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", extractor.extractLawyerAttribute(container, new By[]{By.className("areas-de-atuacao")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551130938333" : socials[1]
        );
    }
}

package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class VanDiepen extends ByNewPage {

    public VanDiepen() {
        super(
                "Van Diepen",
                "https://vandiepen.com/en/lawyer/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"advocaat-list\"]/div/div/div"));
        return div.findElements(By.cssSelector(".jet-listing-grid__item"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions, InterruptedException {
        String link = lawyer.findElement(By.cssSelector("a[href*='https://vandiepen.com/en/lawyer/']")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String firstName = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".elementor-element-d74c9db .jet-listing-dynamic-field__content")}, "NAME", "textContent", LawyerExceptions::nameException);
        String lastName = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".elementor-element-9cc97f1 .jet-listing-dynamic-field__content")}, "NAME", "textContent", LawyerExceptions::nameException);

        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);
        String pa = lawyer.getText();

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.tagName("body"));
        String role = container.getText();
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";


        return Map.of(
                "link", link,
                "name", String.format("%s %s", firstName, lastName),
                "role", role,
                "firm", this.name,
                "country", "the Netherlands",
                "practice_area", pa,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "31229287000" : socials[1]
        );
    }
}

package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MayoraMayora extends ByPage {

    public MayoraMayora() {
        super(
                "Mayora & Mayora",
                "https://mayora-mayora.com/en/team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div.elementor-inner-column"));
        return lawyers;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector("li.elementor-icon-list-item a")), false);

        return Map.of(
                "link", this.link,
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-image-box-title")}, "NAME", LawyerExceptions::nameException),
                "role", "Partner",
                "firm", this.name,
                "country", "Guatemala",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "50222235959" : socials[1]
        );
    }
}

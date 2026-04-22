package org.example.src.sites.to_test._standingBy;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class FCGD extends ByPage {

    public FCGD() {
        super(
                "FCGD",
                "https://www.fcdg.com.br/en/our-team",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.rollDownToBottom(0.4);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("div.framer-1pvcg97"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector("span")), true);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.lawyer-name")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h6")}, "NAME", LawyerExceptions::nameException),
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", "552125290400"
        );
    }
}
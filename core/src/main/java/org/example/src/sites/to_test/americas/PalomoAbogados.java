package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PalomoAbogados extends ByPage {

    public PalomoAbogados() {
        super(
                "Palomo Abogados",
                "https://palomoabogadosen.squarespace.com/federico-palomo-1",
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
        // Based on the snippet, this is a single profile layout being treated as a ByPage item
        return MyDriver.wait.findElements(By.id("yui_3_17_2_1_1777374783480_75"));
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String rawText = lawyer.getAttribute("innerText");
        String[] socials = this.getSocialsFromText(rawText);

        return Map.of(
                "link", this.driver.getCurrentUrl(),
                "name", "Federico Palomo",
                "role", "Founding partner",
                "firm", this.name,
                "country", "Guatemala",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.id("block-20a8728fad6afc6a41be")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "50222797474" : socials[1]
        );
    }
}

package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class Alfaro extends ByPage {

    public Alfaro() {
        super(
                "Alfaro",
                "http://www.alfaromergers.com/members.php?len=en",
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
        WebElement element = MyDriver.wait.findElement(By.xpath("/html/body/table[3]/tbody/tr[2]/td[3]/table/tbody/tr/td[2]/table/tbody/tr[1]/td/ul"));
        return element.findElements(By.cssSelector("li > p"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector("a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.alfa")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("a.alfa")}, "NAME", LawyerExceptions::nameException),
                "role", "Partner",
                "firm", this.name,
                "country", "Argentina",
                "practice_area", "",
                "email", socials[0],
                "phone", "541143225656"
        );
    }
}

package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class HenlinGibsonHenlin extends ByPage {

    public HenlinGibsonHenlin() {
        super(
                "Henlin Gibson Henlin",
                "https://www.henlin.pro/team",
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
        return MyDriver.wait.findElements(By.cssSelector("div.attorney-single"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("h4")}, "NAME", "textContent", LawyerExceptions::nameException);
        String[] split = TreatLawyerParams.treatNameForEmail(name).split(" ");
        String email = split[0] + split[split.length -1] + "@henlin.pro";

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", name,
                "role", extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("p")}, "ROLE", "textContent", LawyerExceptions::roleException),
                "firm", this.name,
                "country", "Jamaica",
                "practice_area", "",
                "email", email,
                "phone", "18769297241"
        );
    }
}

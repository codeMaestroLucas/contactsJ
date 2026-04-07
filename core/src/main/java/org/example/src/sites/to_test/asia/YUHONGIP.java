package org.example.src.sites.to_test.asia;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class YUHONGIP extends ByPage {

    public YUHONGIP() {
        super(
                "YUHONG IP Law Firm,",
                "http://www.yuhongip.com/Partners.html",
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
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("sumary_list"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("newDetail")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("newName")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("newDetail")}, "ROLE", LawyerExceptions::roleException);
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);

        String treated = TreatLawyerParams.treatNameForEmail(name.replace("Mr.", "").replace("Ms.", "").trim());
        String[] parts = treated.split(" ");
        String email = parts[parts.length - 1] + parts[0] + "@yuhongip.com";

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "China",
                "practice_area", "IP",
                "email", email,
                "phone", "xxxx"
        );
    }
}

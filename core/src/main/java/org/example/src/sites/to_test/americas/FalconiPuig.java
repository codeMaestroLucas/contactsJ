package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class FalconiPuig extends ByPage {

    public FalconiPuig() {
        super(
                "Falconi Puig",
                "https://en.falconipuig.com/falconi-puig-team/",
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
        return MyDriver.wait.findElements(By.cssSelector(".stm_staff"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("stm_staff__name")}, "NAME", LawyerExceptions::nameException);
        String email = extractor.extractLawyerText(lawyer, new By[]{By.className("stm_staff__job")}, "EMAIL", LawyerExceptions::emailException);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".stm_staff__name a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", name,
                "role", "Lawyer",
                "firm", this.name,
                "country", "Ecuador",
                "practice_area", "",
                "email", email,
                "phone", "59322561808"
        );
    }
}

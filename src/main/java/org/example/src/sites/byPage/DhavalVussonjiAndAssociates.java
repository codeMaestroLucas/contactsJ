package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class DhavalVussonjiAndAssociates extends ByPage {
    public DhavalVussonjiAndAssociates() {
        super(
                "Dhaval Vussonji And Associates",
                "https://dhavalvussonji.com/partners/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.clickOnAddBtn(By.xpath("//*[@id=\"elementor-popup-modal-944\"]/div/div[2]/div/section[2]/div/div/div/section[1]/div/div[2]/div/div/div/div/a"));
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("section.elementor-inner-section")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='https://dhavalvussonji.com/partners/']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='https://dhavalvussonji.com/partners/']")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String constructEmail(String name) {
        String[] nameParts = TreatLawyerParams.treatNameForEmail(name).split("\\s+");
        String firstName = nameParts[0];
        String lastName = nameParts[nameParts.length - 1];
        return firstName + "." + lastName + "@dvassociates.co.in";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String email = this.constructEmail(name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "India",
                "practice_area", "",
                "email", email,
                "phone", "912266623535"
        );
    }
}

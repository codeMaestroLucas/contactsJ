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

public class GPK extends ByPage {

    private final By[] byRoleArray = {
            By.tagName("p")
    };

    public GPK() {
        super(
                "GPK",
                "https://www.lawfirm.at/en/team",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement lawyersDiv = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"partner\"]/div"))
            );
            return lawyersDiv.findElements(By.className("user-info"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.className("user-link") };
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.className("user-link") };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        // Snippet text: "Admitted to the bar in 1986". Not a role.
        // We will return "Partner" default as these usually are.
        return "Partner";
    }

    public String[] getSocials(String name) {
        // Email format (firstName).(LastName)@lawfirm.at
        try {
            String treatedName = TreatLawyerParams.treatName(name);
            String cleanName = treatedName.toLowerCase().replace(" ", ".");
            String email = cleanName + "@lawfirm.at";
            return new String[]{email, ""};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String[] socials = this.getSocials(name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Austria",
                "practice_area", "",
                "email", socials[0],
                "phone", "430512571811"
        );
    }
}

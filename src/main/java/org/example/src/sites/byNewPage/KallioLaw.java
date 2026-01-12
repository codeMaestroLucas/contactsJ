package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KallioLaw extends ByNewPage {
    String[] validRoles = new String[]{
            "partner", "counsel", "advisor", "senior associate"
    };

    public KallioLaw() {
        super(
                "Kallio Law",
                "https://www.kalliolaw.com/advisors/",
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
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[href*='https://www.kalliolaw.com/advisors/']")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.tagName("h3") };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.tagName("p") };
        String role = extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String[] getSocials(String name, WebElement lawyer) throws LawyerExceptions {
        String treatedName = TreatLawyerParams.treatNameForEmail(name);
        String cleanName = treatedName.replace(" ", ".");
        String email = cleanName + "@kalliolaw.fi";

        try {

            String phone = lawyer.findElement(By.cssSelector("a[herf*='tel']")).getText();

            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{email, ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement element = driver.findElement(By.xpath("//div[starts-with(@id,'post-')]/div/div[1]/div/div[2]"));

        String role = this.getRole(element);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String name = this.getName(element);
        String[] socials = this.getSocials(name, element);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Finland",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "48223395400" : socials[1]
        );
    }
}

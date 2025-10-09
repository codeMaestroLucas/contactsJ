package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Vischer extends ByNewPage {
    private final String[] validRoles = new String[]{
            "partner", "counsel", "managing associate", "senior associate"
    };

    private final By[] byRoleArray = {
            By.cssSelector("li.title")
    };

    public Vischer() {
        super(
                "Vischer",
                "https://www.vischer.com/en/team/find-team-members/",
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
                            By.cssSelector("a[href*='https://www.vischer.com/en/team/']")
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
        By[] byArray = new By[]{
                By.tagName("h1")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String role = lawyer.findElement(By.className("title")).getAttribute("textContent");
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String getPracticeArea() throws LawyerExceptions {
        try {
            return driver.findElement(
                    By.xpath("//*[@id=\"content\"]/div/div/div/div[2]/div/div/div/div/div/div[2]/div[1]/div[6]/details/div/div/div/ul/li[1]")
            ).getAttribute("textContent");
        } catch (Exception e) {
            return "";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        WebElement div = lawyer.findElement(By.className("contacts"));
        String phone = div
                .findElement(By.className("phone"))
                .findElement(By.className("value"))
                .getAttribute("textContent");
        String email = div.findElement(By.tagName("a")).getAttribute("href");
        return new String[]{email, phone};
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("info"));

        String role = this.getRole(div);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", role,
                "firm", this.name,
                "country", "Switzerland",
                "practice_area", this.getPracticeArea(),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}
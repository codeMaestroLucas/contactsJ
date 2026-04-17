package org.example.src.sites.americas;

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

public class PGLaw extends ByNewPage {

    public PGLaw() {
        super(
                "PG Law",
                "https://pg.law/en/team/",
                1
        );
    }

    private final By[] byRoleArray = {By.className("elementor-flip-box__layer__description")};

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("elementor-flip-box__back")));

            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    public String getName()  {
        return driver.findElement(By.xpath("/html/body/div[5]/div[1]/div/div[2]/div[2]/div/div[2]/div[1]/div/h2")).getAttribute("textContent");
    }

    private String getRole() {
        return driver.findElement(By.xpath("/html/body/div[5]/div[1]/div/div[2]/div[2]/div/div[2]/div[2]/div/p")).getAttribute("textContent");
    }

    private String getPhone() {
        try {
            return driver.findElement(By.xpath("/html/body/div[5]/div[1]/div/div[2]/div[2]/div/div[2]/div[3]/div/p")).getAttribute("textContent");
        } catch (Exception e) {
            return "551130850089";
        }
    }

    private String getEmail(String name) {
        String[] parts = TreatLawyerParams.treatNameForEmail(name).split(" ");
        return parts[0] + "." + parts[parts.length - 1] + "@pg.law";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        String name = this.getName();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", this.getRole(),
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", this.getEmail(name),
                "phone", this.getPhone()
        );
    }
}

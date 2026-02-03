package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class BSJP extends ByPage {

    public BSJP() {
        super(
                "BSJP",
                "https://bsjp.pl/en/people/",
                1
        );
    }

    private String[] validRoles = {"partner", "counsel", "senior associate"};

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
                            By.className("people-container")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        String name = lawyer.getAttribute("data-nazwa");
        if (name == null || name.isEmpty()) throw LawyerExceptions.nameException("Name attribute is empty");
        return name;
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String role = lawyer.getAttribute("data-opis");
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String getEmail(WebElement lawyer) throws LawyerExceptions {
        String email = lawyer.getAttribute("data-email");
        if (email == null || email.isEmpty()) throw LawyerExceptions.emailException("Email attribute is empty");
        return email;
    }

    private String getPhone(WebElement lawyer) {
        String phone = lawyer.getAttribute("data-telefon");
        return phone == null ? "" : phone;
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String role = this.getRole(lawyer);
        if (role.equals("Invalid Role")) return "Invalid Role";

        return Map.of(
                "link", this.link,
                "name", this.getName(lawyer),
                "role", role,
                "firm", this.name,
                "country", "Poland",
                "practice_area", "",
                "email", this.getEmail(lawyer),
                "phone", this.getPhone(lawyer)
        );
    }
}
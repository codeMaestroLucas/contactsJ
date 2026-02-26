package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HoxhaMemiAndHoxha extends ByPage {

    public HoxhaMemiAndHoxha() {
        super(
                "Hoxha, Memi & Hoxha",
                "https://www.hmh.al/team",
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
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("[data-testid='mesh-container-content']")
                    )
            );

            // Indexes to remove
            Set<Integer> indexesToRemove = Set.of(0, 1, 2, 3, 4, 5, 6, 13);

            List<WebElement> filteredLawyers = new ArrayList<>();
            for (int i = 0; i < lawyers.size(); i++) {
                if (!indexesToRemove.contains(i)) {
                    filteredLawyers.add(lawyers.get(i));
                }
            }

            filteredLawyers.removeLast();
            filteredLawyers.removeLast();
            return filteredLawyers;

        } catch (Exception e) {
            return List.of();
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='https://www.hmh.al/']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h6")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String[] getSocials(String name) {
        String treatedName = TreatLawyerParams.treatNameForEmail(name);
        String[] parts = treatedName.split("\\s+");
        if (parts.length >= 2) {
            String email = parts[0] + "." + parts[1] + "@hmh.al";
            return new String[]{email, ""};
        }
        return new String[]{"", ""};
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String[] socials = this.getSocials(name);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Albania",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35542274558" : socials[1]
        );
    }
}
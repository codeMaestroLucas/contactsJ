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

public class GaiaSilvaGaedeAndAssociados extends ByNewPage {

    private final By[] byRoleArray = {
            By.className("team-card__designation")
    };

    public GaiaSilvaGaedeAndAssociados() {
        super(
                "Gaia Silva Gaede & Associados",
                "https://gsga.com.br/en/profissionais/?filtro=true&filter_name=&cargo=partner",
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
        String[] validRoles = new String[]{"partner"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("team-card")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".team-card__title a")};
        // O link está dentro de um onclick: onclick="this.href=urlIdioma('profissional/...') "
        String onclick = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "onclick", LawyerExceptions::linkException);

        String path = onclick.split("'")[1];
        String fullUrl = "https://gsga.com.br/en/" + path;

        MyDriver.openNewTab(fullUrl);
        return fullUrl;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("team-card__title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String text = extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
        return text.split("/")[0].trim();
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector(".team-details__list a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        // Captura dados da lista antes de navegar
        String name = this.getName(lawyer);
        String role = this.getRole(lawyer);

        this.openNewTab(lawyer);

        // Agora na aba do perfil (HTML2)
        WebElement container = driver.findElement(By.className("team-details__list"));
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "551137774800" : socials[1]
        );
    }
}

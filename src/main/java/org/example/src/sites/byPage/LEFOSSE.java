package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LEFOSSE extends ByPage {
    public LEFOSSE() {
        super(
            "LEFOSSE",
            "",
            11
        );
    }

    protected void accessPage(int index) throws InterruptedException {
        int pgNumber = index < 8 ? index + 1: index -7;

        String url = String.format(
                "https://lefosse.com/en/page/%d/?post_type=advogados&s&atuacao&cargo=%s",
                pgNumber,
                index < 8 ? "partner" : "counsel-en"
        );

        driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000);
    }

    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("float")
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='https://lefosse.com/en/advogado/']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='https://lefosse.com/en/advogado/']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("title");
    }


    private String getRole(WebElement lawyer) {
        return Objects.requireNonNull(driver.getCurrentUrl()).toLowerCase().contains("partner") ? "Partner" : "Counsel";
    }


    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = new By[]{
                    By.className("font-xsmall"),
                    By.cssSelector("p")
            };
            WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
            String html = element.getAttribute("outerHTML");

            Pattern pattern = Pattern.compile("partner for</span>.*?<span[^>]*>(.*?)</span>", Pattern.CASE_INSENSITIVE);
            assert html != null;

            Matcher matcher = pattern.matcher(html);

            if (matcher.find()) {
                return matcher.group(1);
            }

            return "";

        } catch (Exception e) {
            return "";
        }
    }


    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("adv-info"))
                        .findElements(By.cssSelector("p"));

            for (WebElement social : socials) {
                String value = siteUtl.getContentFromTag(social.getAttribute("outerHTML")).toLowerCase().trim();

                // Check if it's an email
                if ((value.contains("mail") || value.contains("@")) && email.isEmpty()) {
                    email = value;
                }

                // Check if it's a valid phone number
                else if ((
                        value.contains("tel") || value.contains("+") || value.contains("phone") ||
                                value.matches(".*\\d{5,}.*")) && phone.isEmpty()) {
                    String cleaned = value.replaceAll("[^0-9]", "");
                    if (cleaned.length() > 5) { // To prevent if an invalid value has been set to phone
                        phone = cleaned;
                    }
                }

                if (!email.isEmpty() && !phone.isEmpty()) break;
            }

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[] { email, phone };

        }
        return new String[] { email, phone };
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
            "link", this.getLink(lawyer),
            "name", this.getName(lawyer),
            "role", this.getRole(lawyer),
            "firm", this.name,
            "country", "Brazil",
            "practice_area", this.getPracticeArea(lawyer),
            "email", socials[0],
            "phone", socials[1]
        );
    }
}

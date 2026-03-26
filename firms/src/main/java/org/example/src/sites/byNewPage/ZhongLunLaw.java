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

public class ZhongLunLaw extends ByNewPage {

    public ZhongLunLaw() {
        super(
                "Zhong Lun Law",
                "https://www.zhonglun.com.hk/en/ourpeople.php",
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
            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"col4\"]/div[2]/div[2]/div/div[1]")
                    )
            );
            List<WebElement> lawyers = div.findElements(By.className("box_names2"));

            div = driver.findElement(By.xpath("//*[@id=\"col4\"]/div[3]/div/div[1]"));
            lawyers.addAll(div.findElements(By.className("box_names2")));

            div = driver.findElement(By.xpath("//*[@id=\"col4\"]/div[5]/div/div[1]"));
            lawyers.addAll(div.findElements(By.className("box_names2")));

            return lawyers;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("plp_name")};
        String name = extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
        return name.split("\n")[0].trim();
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("plp_tlt")};
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            return lawyer.findElement(By.xpath("//span[contains(text(),'PRACTICE AREAS')]/../following-sibling::a")).getText();
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector("a.plp_contacttxt4, div.sbcol1-1_subhead"));
            String email = socials.get(socials.size() - 1).getText().trim();
            String phone = socials.get(0).getText().trim();
            return new String[]{email, phone};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.className("plp_ct"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "Hong Kong",
                "practice_area", this.getPracticeArea(div.findElement(By.xpath("./.."))),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}

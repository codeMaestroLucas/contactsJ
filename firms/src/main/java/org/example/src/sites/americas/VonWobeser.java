package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class VonWobeser extends ByPage {

    public VonWobeser() {
        super(
                "Von Wobeser",
                "https://vonwobeser.com/index.php/lawyer?l=141",
                41
        );
    }

    String[] links = {
            "https://vonwobeser.com/index.php/lawyer?l=141",
            "https://vonwobeser.com/index.php/lawyer?l=143",
            "https://vonwobeser.com/index.php/lawyer?l=144",
            "https://vonwobeser.com/index.php/lawyer?l=145",
            "https://vonwobeser.com/index.php/lawyer?l=147",
            "https://vonwobeser.com/index.php/lawyer?l=148",
            "https://vonwobeser.com/index.php/lawyer?l=149",
            "https://vonwobeser.com/index.php/lawyer?l=175",
            "https://vonwobeser.com/index.php/lawyer?l=189",
            "https://vonwobeser.com/index.php/lawyer?l=168",
            "https://vonwobeser.com/index.php/lawyer?l=160",
            "https://vonwobeser.com/index.php/lawyer?l=195",
            "https://vonwobeser.com/index.php/lawyer?l=323",
            "https://vonwobeser.com/index.php/lawyer?l=165",
            "https://vonwobeser.com/index.php/lawyer?l=186",
            "https://vonwobeser.com/index.php/lawyer?l=224",
            "https://vonwobeser.com/index.php/lawyer?l=355",
            "https://vonwobeser.com/index.php/lawyer?l=187",
            "https://vonwobeser.com/index.php/lawyer?l=366",
            "https://vonwobeser.com/index.php/lawyer?l=367",
            "https://vonwobeser.com/index.php/lawyer?l=253",
            "https://vonwobeser.com/index.php/lawyer?l=356",
            "https://vonwobeser.com/index.php/lawyer?l=155",
            "https://vonwobeser.com/index.php/lawyer?l=291",
            "https://vonwobeser.com/index.php/lawyer?l=368",
            "https://vonwobeser.com/index.php/lawyer?l=396",
            "https://vonwobeser.com/index.php/lawyer?l=134",
            "https://vonwobeser.com/index.php/lawyer?l=152",
            "https://vonwobeser.com/index.php/lawyer?l=135",
            "https://vonwobeser.com/index.php/lawyer?l=304",
            "https://vonwobeser.com/index.php/lawyer?l=140",
            "https://vonwobeser.com/index.php/lawyer?l=151",
            "https://vonwobeser.com/index.php/lawyer?l=444",
            "https://vonwobeser.com/index.php/lawyer?l=348",
            "https://vonwobeser.com/index.php/lawyer?l=179",
            "https://vonwobeser.com/index.php/lawyer?l=178",
            "https://vonwobeser.com/index.php/lawyer?l=218",
            "https://vonwobeser.com/index.php/lawyer?l=321",
            "https://vonwobeser.com/index.php/lawyer?l=417",
            "https://vonwobeser.com/index.php/lawyer?l=330",
            "https://vonwobeser.com/index.php/lawyer?l=190"
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(links[index]);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.cssSelector("div.attorney__meta"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("attorney__meta--name")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("attorney__meta--role")}, "ROLE", "textContent", LawyerExceptions::roleException);
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        String email = socials[0].split("mail=")[1].replace("&country=mx", "");
        String phone = socials[0].split("tel=")[1].split("mail=")[0];
        return Map.of(
                "link", this.link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".attorney__meta--list li:first-child ul")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException),
                "email", email,
                "phone", phone.isEmpty() ? "525552581003" : phone
        );
    }
}

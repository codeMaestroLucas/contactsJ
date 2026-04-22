package org.example.src.sites.americas;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GoodrichRiquelme extends ByPage {

    public GoodrichRiquelme() {
        super(
                "Goodrich Riquelme",
                "",
                15
        );
    }

    String[] links = {
            "https://goodrichriquelme.com/diaz-enrique/",
            "https://goodrichriquelme.com/enriquez-david-3/",
            "https://goodrichriquelme.com/perez-delgado-luis/",
            "https://goodrichriquelme.com/sosa-guillermo/",
            "https://goodrichriquelme.com/our-people/delgado-jaime/",
            "https://goodrichriquelme.com/our-people/leon-orantes-jorge/",
            "https://goodrichriquelme.com/our-people/moreyra-raul/",
            "https://goodrichriquelme.com/our-people/urdapilleta-agustin/",
            "https://goodrichriquelme.com/our-people/flores-julio/",
            "https://goodrichriquelme.com/our-people/suarez-juan-carlos/",
            "https://goodrichriquelme.com/prieto-julio/",
            "https://goodrichriquelme.com/our-people/sandoval-jorge/",
            "https://goodrichriquelme.com/our-people/garcia-gustavo/",
            "https://goodrichriquelme.com/our-people/esquivel-juan/",
            "https://goodrichriquelme.com/our-people/gomez-teresa/"
    };

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(links[index]);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.xpath("//*[@id=\"jupiterx-main\"]/div/section/div/div/div/section/div/div[1]/div"));
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = lawyer.findElement(By.tagName("h1")).getText() + " " + lawyer.findElement(By.tagName("h2")).getText();
        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);
        String paRole = lawyer.findElement(By.xpath("//*[@id=\"jupiterx-main\"]/div/section/div/div/div/section/div/div[2]/div/div[2]/div/p")).getText();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", paRole,
                "firm", this.name,
                "country", "Mexico",
                "practice_area", paRole,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "525555251422" : socials[1]
        );
    }
}

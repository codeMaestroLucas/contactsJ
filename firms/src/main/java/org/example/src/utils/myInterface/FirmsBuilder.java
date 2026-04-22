package org.example.src.utils.myInterface;

import org.example.src.entities.BaseSites.Site;
import org.example.src.utils.ContinentConfig;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Dynamically discovers all firm classes by scanning each continent's package.
 * To register a new firm, simply place its class in the correct continent package
 * under org.example.src.sites.<continent> — no changes needed here.
 */
public class FirmsBuilder {

    private static final String BASE_PACKAGE = "org.example.src.sites";

    private static Site[] scanContinent(String continent) {
        Reflections reflections = new Reflections(BASE_PACKAGE + "." + continent);
        return reflections.getSubTypesOf(Site.class).stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .map(c -> {
                    try {
                        return (Site) c.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to instantiate " + c.getName(), e);
                    }
                })
                .toArray(Site[]::new);
    }

    public static Site[] getAfrica()   { return scanContinent("africa"); }
    public static Site[] getAsia()     { return scanContinent("asia"); }
    public static Site[] getEurope()   { return scanContinent("europe"); }
    public static Site[] getAmericas() { return scanContinent("americas"); }
    public static Site[] getOceania()  { return scanContinent("oceania"); }
    public static Site[] getMundial()  { return scanContinent("mundial"); }

    public static Site[] build() {
        List<Site> sites = new ArrayList<>();

        if (ContinentConfig.isContinentEnabled("Africa"))   sites.addAll(Arrays.asList(getAfrica()));
        if (ContinentConfig.isContinentEnabled("Asia"))     sites.addAll(Arrays.asList(getAsia()));
        if (ContinentConfig.isContinentEnabled("Europe"))   sites.addAll(Arrays.asList(getEurope()));
        if (ContinentConfig.isContinentEnabled("Americas")) sites.addAll(Arrays.asList(getAmericas()));
        if (ContinentConfig.isContinentEnabled("Oceania"))  sites.addAll(Arrays.asList(getOceania()));
        sites.addAll(Arrays.asList(getMundial()));

        return sites.toArray(new Site[0]);
    }
}

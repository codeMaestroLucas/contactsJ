package entities.BaseSites;

import java.io.IOException;
import java.nio.file.*;

public class FirmMover {

    private static final String CORE_TO_TEST_BASE =
            "core/src/main/java/org/example/src/sites/to_test/";
    private static final String FIRMS_SITES_BASE =
            "firms/src/main/java/org/example/src/sites/";
    private static final String FIRMS_BUILDER_PATH =
            "firms/src/main/java/org/example/src/utils/myInterface/FirmsBuilder.java";

    public static void move(String className, String continent) throws IOException {
        Path source = Paths.get(CORE_TO_TEST_BASE + continent + "/" + className + ".java");
        Path dest = Paths.get(FIRMS_SITES_BASE + continent + "/" + className + ".java");

        String content = Files.readString(source);

        // Update package declaration
        content = content.replace(
                "package org.example.src.sites.to_test." + continent + ";",
                "package org.example.src.sites." + continent + ";"
        );

        Files.writeString(dest, content);
        Files.delete(source);

        registerInFirmsBuilder(className, continent);

        System.out.println("✓ Movido para " + dest);
        System.out.println("✓ Registrado em FirmsBuilder." + continent.toUpperCase());
    }

    private static void registerInFirmsBuilder(String className, String continent) throws IOException {
        String arrayName;
        switch (continent.toLowerCase()) {
            case "americas" -> arrayName = "AMERICAS";
            case "europe"   -> arrayName = "EUROPE";
            case "asia"     -> arrayName = "ASIA";
            case "africa"   -> arrayName = "AFRICA";
            case "oceania"  -> arrayName = "OCEANIA";
            case "mundial"  -> arrayName = "MUNDIAL";
            default -> throw new IllegalArgumentException("Continente desconhecido: " + continent);
        }

        Path builderPath = Paths.get(FIRMS_BUILDER_PATH);
        String content = Files.readString(builderPath);

        // Find the array declaration
        String arrayDecl = "private static final Site[] " + arrayName + " = {";
        int arrayStart = content.indexOf(arrayDecl);
        if (arrayStart == -1) throw new IllegalStateException("Array " + arrayName + " não encontrado no FirmsBuilder");

        // Find the closing }; after this array
        int closingBrace = content.indexOf("\n    };", arrayStart);
        if (closingBrace == -1) throw new IllegalStateException("Fechamento do array " + arrayName + " não encontrado");

        String newEntry = "\n            new " + className + "(),";
        content = content.substring(0, closingBrace) + newEntry + content.substring(closingBrace);

        Files.writeString(builderPath, content);
    }
}

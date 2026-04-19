package entities.BaseSites;

import org.example.src.entities.BaseSites.Site;
import org.example.src.entities.MyDriver;
import org.example.src.utils.Validations;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class BatchTestRunner {

    private static final String TO_TEST_SRC =
            "core/src/main/java/org/example/src/sites/to_test/";
    private static final String TO_TEST_PKG = "org.example.src.sites.to_test.";

    private static final List<String> CONTINENT_ORDER =
            List.of("americas", "europe", "asia", "africa", "oceania", "mundial");

    public static void main(String[] args) throws Exception {
        MyDriver.setHeadless(true);
        Validations.enableTestMode();

        List<FirmEntry> firms = discoverFirms();
        Scanner scanner = new Scanner(System.in);
        int total = firms.size();

        System.out.printf("%n═══ BatchTestRunner: %d firmas encontradas ═══%n%n", total);

        for (int i = 0; i < firms.size(); i++) {
            FirmEntry entry = firms.get(i);
            System.out.printf("[%d/%d] %s (%s)%n", i + 1, total, entry.className, entry.continent);
            System.out.println("─".repeat(60));

            Validations.resetTestModeCount();
            long start = System.currentTimeMillis();

            boolean crashed = false;
            try {
                Site site = instantiate(entry);
                site.searchForLawyers(true).run();
            } catch (Exception e) {
                System.err.println("ERRO ao rodar " + entry.className + ": " + e.getMessage());
                crashed = true;
            }

            long elapsed = System.currentTimeMillis() - start;
            int extracted = Validations.getTestModeCount();

            System.out.println("─".repeat(60));
            System.out.printf("=> Extraiu %d advogado(s) em %.1fs%s%n%n",
                    extracted, elapsed / 1000.0, crashed ? " [com erro]" : "");

            String action = prompt(scanner, entry.className);
            switch (action) {
                case "a" -> {
                    try {
                        FirmMover.move(entry.className, entry.continent);
                    } catch (IOException e) {
                        System.err.println("Erro ao mover: " + e.getMessage());
                    }
                }
                case "s" -> System.out.println("Skipado.");
                case "q" -> {
                    System.out.println("Encerrando.");
                    return;
                }
                case "r" -> {
                    i--; // retry same firm
                    System.out.println("Repetindo...");
                }
            }
            System.out.println();
        }

        System.out.println("✓ Todas as firmas processadas.");
    }

    private static String prompt(Scanner scanner, String firmName) {
        while (true) {
            System.out.printf("[A]provar e mover / [S]kipar / [Q]uit / [R]etry ? ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (List.of("a", "s", "q", "r").contains(input)) return input;
            System.out.println("Opção inválida. Use A, S, Q ou R.");
        }
    }

    private static List<FirmEntry> discoverFirms() throws IOException {
        List<FirmEntry> result = new ArrayList<>();
        for (String continent : CONTINENT_ORDER) {
            Path dir = Paths.get(TO_TEST_SRC + continent);
            if (!Files.isDirectory(dir)) continue;
            try (Stream<Path> files = Files.list(dir)) {
                files.filter(p -> p.toString().endsWith(".java"))
                        .map(p -> {
                            String name = p.getFileName().toString().replace(".java", "");
                            return new FirmEntry(name, continent);
                        })
                        .sorted(Comparator.comparing(e -> e.className))
                        .forEach(result::add);
            }
        }
        return result;
    }

    private static Site instantiate(FirmEntry entry) throws Exception {
        String fqn = TO_TEST_PKG + entry.continent + "." + entry.className;
        Class<?> clazz = Class.forName(fqn);
        Site site = (Site) clazz.getDeclaredConstructor().newInstance();

        Field totalPages = Site.class.getDeclaredField("totalPages");
        totalPages.setAccessible(true);
        if (totalPages.getInt(site) != 1) totalPages.setInt(site, 100);

        Field maxLawyers = Site.class.getDeclaredField("maxLawyersForSite");
        maxLawyers.setAccessible(true);
        maxLawyers.setInt(site, 100);

        return site;
    }

    private record FirmEntry(String className, String continent) {}
}

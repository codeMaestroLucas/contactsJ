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

        Map<String, List<FirmEntry>> byContinent = discoverByContinent();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String chosen = selectContinent(scanner, byContinent);
            if (chosen == null) {
                System.out.println("Encerrando.");
                break;
            }

            List<FirmEntry> firms = byContinent.get(chosen);
            boolean quit = runContinent(scanner, firms, chosen);
            if (quit) {
                System.out.println("Encerrando.");
                break;
            }

            System.out.printf("%n✓ Pasta '%s' concluída.%n", chosen);
        }
    }

    private static String selectContinent(Scanner scanner, Map<String, List<FirmEntry>> byContinent) {
        List<String> available = CONTINENT_ORDER.stream()
                .filter(byContinent::containsKey)
                .toList();

        if (available.isEmpty()) {
            System.out.println("Nenhuma firma encontrada em nenhuma pasta.");
            return null;
        }

        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║         Selecione uma pasta para testar      ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        for (int i = 0; i < available.size(); i++) {
            String c = available.get(i);
            int count = byContinent.get(c).size();
            System.out.printf("║  [%d] %-30s %3d firma(s) ║%n", i + 1, c, count);
        }
        System.out.println("║  [0] Sair                                    ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        while (true) {
            System.out.print("Escolha: ");
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice == 0) return null;
                if (choice >= 1 && choice <= available.size()) return available.get(choice - 1);
            } catch (NumberFormatException ignored) {}
            System.out.printf("Opção inválida. Digite um número entre 0 e %d.%n", available.size());
        }
    }

    /** Returns true if the user chose to quit mid-run. */
    private static boolean runContinent(Scanner scanner, List<FirmEntry> firms, String continent) {
        int total = firms.size();
        System.out.printf("%n═══ %s: %d firma(s) encontrada(s) ═══%n%n", continent.toUpperCase(), total);

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

            String action = promptFirm(scanner, entry.className);
            switch (action) {
                case "a" -> {
                    try {
                        FirmMover.move(entry.className, entry.continent);
                    } catch (IOException e) {
                        System.err.println("Erro ao mover: " + e.getMessage());
                    }
                }
                case "s" -> System.out.println("Skipado.");
                case "q" -> { return true; }
                case "r" -> {
                    i--;
                    System.out.println("Repetindo...");
                }
            }
            System.out.println();
        }
        return false;
    }

    private static String promptFirm(Scanner scanner, String firmName) {
        while (true) {
            System.out.printf("[A]provar e mover / [S]kipar / [Q]uit / [R]etry ? ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (List.of("a", "s", "q", "r").contains(input)) return input;
            System.out.println("Opção inválida. Use A, S, Q ou R.");
        }
    }

    private static Map<String, List<FirmEntry>> discoverByContinent() throws IOException {
        Map<String, List<FirmEntry>> result = new LinkedHashMap<>();
        for (String continent : CONTINENT_ORDER) {
            Path dir = Paths.get(TO_TEST_SRC + continent);
            if (!Files.isDirectory(dir)) continue;
            List<FirmEntry> firms = new ArrayList<>();
            try (Stream<Path> files = Files.list(dir)) {
                files.filter(p -> p.toString().endsWith(".java"))
                        .map(p -> {
                            String name = p.getFileName().toString().replace(".java", "");
                            return new FirmEntry(name, continent);
                        })
                        .sorted(Comparator.comparing(e -> e.className))
                        .forEach(firms::add);
            }
            if (!firms.isEmpty()) result.put(continent, firms);
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

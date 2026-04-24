package entities.BaseSites;

import org.example.src.entities.BaseSites.Site;
import org.example.src.entities.MyDriver;
import org.example.src.utils.Validations;
import org.reflections.Reflections;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class BatchTestRunner {

    private static final boolean HEADLESS     = true;
    private static final int     MAX_LAWYERS  = 3;
    private static final int     WIDTH        = 62;
    private static final String  BASE_PACKAGE = "org.example.src.sites.to_test";
    private static final String  APPROVED_FILE = "data/approvedBatchFirms.txt";

    private static final List<String> CONTINENTS =
            List.of("africa", "americas", "asia", "europe", "mundial", "oceania");

    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        // ── Descobre firmas no classpath ──────────────────────────────
        Reflections reflections = new Reflections(BASE_PACKAGE);
        Set<Class<? extends Site>> allSites = reflections.getSubTypesOf(Site.class);

        Map<String, List<Class<?>>> byContinent = new LinkedHashMap<>();
        for (String continent : CONTINENTS) {
            String pkg = BASE_PACKAGE + "." + continent + ".";
            List<Class<?>> firms = allSites.stream()
                    .filter(c -> c.getName().startsWith(pkg))
                    .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                    .sorted(Comparator.comparing(Class::getSimpleName))
                    .collect(Collectors.toList());
            if (!firms.isEmpty()) byContinent.put(continent, firms);
        }

        // ── Cabeçalho ─────────────────────────────────────────────────
        printBox("Batch Test Runner");

        // ── Seleção de continente ─────────────────────────────────────
        List<String> continents = new ArrayList<>(byContinent.keySet());
        bar();
        bar("Selecione o continente:");
        bar();
        for (int i = 0; i < continents.size(); i++) {
            String c = continents.get(i);
            bar("  " + (i + 1) + ". " + c + "  (" + byContinent.get(c).size() + " firmas)");
        }
        bar();
        System.out.print("| Opção: ");
        int ci = Integer.parseInt(sc.nextLine().trim()) - 1;
        bar();
        printLine();

        String continent = continents.get(ci);

        // ── Filtra firmas já aprovadas em execuções anteriores ────────
        Set<String> persisted = loadApproved(continent);

        List<Class<?>> firms = byContinent.get(continent).stream()
                .filter(c -> !persisted.contains(c.getSimpleName()))
                .collect(Collectors.toList());

        if (firms.isEmpty()) {
            printBox("Todas as firmas já aprovadas");
            bar();
            bar("  Nenhuma firma pendente em '" + continent + "'.");
            bar();
            printLine();
            sc.close();
            return;
        }

        int total = byContinent.get(continent).size();
        int done  = persisted.size();

        printBox("Retomando: \u001B[32m" + done + "/" + total + "\u001B[0m aprovadas — \u001B[33m" + firms.size() + "\u001B[0m pendentes");

        // ── Execução sequencial (ordem alfabética) ────────────────────
        List<String> sessionApproved = new ArrayList<>();

        for (int i = 0; i < firms.size(); i++) {
            Class<?> clazz = firms.get(i);
            String firmName = clazz.getSimpleName();

            boolean repeat;
            boolean currentHeadless = HEADLESS;
            do {
                repeat = false;
                MyDriver.setHeadless(currentHeadless);

                Site site = buildSite(clazz);

                printBox(firmName + " [" + (done + i + 1) + "/" + total + "]");
                bar("LINK: " + site.getLink());
                bar();

                Validations.enableTestMode();
                Validations.resetTestModeCount();
                site.searchForLawyers(true);

                // ── Prompt pós-teste ──────────────────────────────────
                System.out.println();
                System.out.println("1. Aprovar e Continuar");
                System.out.println("2. Repetir a firma");
                System.out.println("3. Repetir sem headless");
                System.out.println("4. Aprovar e Sair");
                System.out.println("5. Sair");
                System.out.print("Opção: ");
                String opt = sc.nextLine().trim();

                switch (opt) {
                    case "1" -> {
                        appendApproved(continent, firmName);
                        sessionApproved.add(firmName);
                    }
                    case "2" -> {
                        if (!currentHeadless) MyDriver.restartDriver();
                        repeat = true;
                        currentHeadless = HEADLESS;
                    }
                    case "3" -> {
                        MyDriver.restartDriver();
                        repeat = true;
                        currentHeadless = false;
                    }
                    case "4" -> {
                        appendApproved(continent, firmName);
                        sessionApproved.add(firmName);
                        i = firms.size();
                    }
                    default -> i = firms.size();
                }
            } while (repeat);

            if (!currentHeadless) {
                MyDriver.restartDriver();
                MyDriver.setHeadless(HEADLESS);
            }
        }

        // ── Resultado final ───────────────────────────────────────────
        printBox("Aprovadas nesta sessão");
        bar();
        if (sessionApproved.isEmpty()) {
            bar("  Nenhuma firma aprovada.");
        } else {
            for (int i = 0; i < sessionApproved.size(); i++) {
                bar("  " + (i + 1) + ". " + sessionApproved.get(i));
            }
        }
        bar();
        printLine();

        sc.close();
    }

    // ── Persistência ──────────────────────────────────────────────────

    private static Set<String> loadApproved(String continent) throws IOException {
        Set<String> approved = new LinkedHashSet<>();
        File file = new File(APPROVED_FILE);
        if (!file.exists()) return approved;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(continent + "/")) {
                    approved.add(line.substring(continent.length() + 1));
                }
            }
        }
        return approved;
    }

    private static void appendApproved(String continent, String firmName) throws IOException {
        String entry = continent + "/" + firmName;
        File file = new File(APPROVED_FILE);

        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().equals(entry)) return;
                }
            }
        } else {
            file.getParentFile().mkdirs();
        }

        try (FileWriter fw = new FileWriter(file, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(entry);
        }
    }

    // ── Site builder ──────────────────────────────────────────────────

    private static Site buildSite(Class<?> clazz) throws Exception {
        Site site = (Site) clazz.getDeclaredConstructor().newInstance();

        Field totalPagesField = Site.class.getDeclaredField("totalPages");
        totalPagesField.setAccessible(true);

        Field maxLawyersField = Site.class.getDeclaredField("maxLawyersForSite");
        maxLawyersField.setAccessible(true);

        maxLawyersField.setInt(site, MAX_LAWYERS);
        if (totalPagesField.getInt(site) != 1) {
            totalPagesField.setInt(site, MAX_LAWYERS);
        }

        return site;
    }

    // ── Helpers de UI ─────────────────────────────────────────────────

    private static void printBox(String title) {
        String border = "=".repeat(WIDTH);
        int pad   = WIDTH - 2 - title.length();
        int left  = pad / 2;
        int right = pad - left;
        System.out.println(border);
        System.out.println("|" + " ".repeat(left) + title + " ".repeat(right) + "|");
        System.out.println(border);
    }

    private static void bar(String text) {
        int pad = WIDTH - 2 - 1 - text.length();
        System.out.println("| " + text + " ".repeat(Math.max(0, pad)) + "|");
    }

    private static void bar() {
        System.out.println("|" + " ".repeat(WIDTH - 2) + "|");
    }

    private static void printLine() {
        System.out.println("=".repeat(WIDTH));
    }
}

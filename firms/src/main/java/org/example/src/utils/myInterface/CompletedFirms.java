package org.example.src.utils.myInterface;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.example.src.CONFIG;
import org.example.src.entities.BaseSites.Site;
import org.example.src.entities.excel.ContactsAlreadyRegisteredSheet;
import org.example.src.utils.ContinentConfig;
import org.example.src.utils.FirmsExhausted;
import org.example.src.utils.FirmsOMonth;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;

@Getter
public class CompletedFirms {
    // ==================== MENU ====================

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n" + "=".repeat(45));
            System.out.println("  1. Ver todas as firmas");
            System.out.println("  2. Verificar estado do sistema");
            System.out.println("  3. Firmas disponíveis");
            System.out.println("  4. Firmas esgotadas");
            System.out.println("  0. Sair");
            System.out.println("=".repeat(45));
            System.out.print("Escolha uma opcao: ");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> showAllFirmsCompleted();
                case "2" -> showSystemState();
                case "3" -> showAvailableFirms();
                case "4" -> showExhaustedFirms();
                case "0" -> {
                    saveSnapshot();
                    scanner.close();
                    return;
                }
                default -> System.out.println(" " + RED + "Opcao invalida." + RESET);
            }
        }
    }


    // FUNCTIONS

    private static Site[] getFirms() { return FirmsBuilder.build(); }

    public final static MyInterfaceUtls interfaceUtls = MyInterfaceUtls.getINSTANCE();

    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String BOLD = "\u001B[1m";
    private static final String DIM = "\u001B[2m";


    // Continent name → getter
    private static final Map<String, Supplier<Site[]>> BY_CONTINENT_GETTERS = Map.of(
            "Africa",   FirmsBuilder::getAfrica,
            "Asia",     FirmsBuilder::getAsia,
            "Europe",   FirmsBuilder::getEurope,
            "Americas", FirmsBuilder::getAmericas,
            "Oceania",  FirmsBuilder::getOceania
    );


    /**
     * Construct all firms ordered by continent weight (highest first), shuffled within each weight group.
     * Firms already registered in monthFirms.txt are excluded.
     * Mundial firms always have weight 0 (lowest priority).
     */
    public static List<Site> constructFirms() {
        // Group enabled continents by weight (descending)
        Map<Integer, List<String>> continentsByWeight = new TreeMap<>(Collections.reverseOrder());

        for (Map.Entry<String, ContinentConfig.ContinentSettings> entry : ContinentConfig.getConfig().entrySet()) {
            if (entry.getValue().isEnabled()) {
                int weight = entry.getValue().getWeight();
                continentsByWeight.computeIfAbsent(weight, k -> new ArrayList<>()).add(entry.getKey());
            }
        }

        List<Site> result = new ArrayList<>();

        // Process each weight group (highest weight first thanks to reverse TreeMap)
        for (Map.Entry<Integer, List<String>> weightGroup : continentsByWeight.entrySet()) {
            List<Site> groupFirms = new ArrayList<>();

            for (String continent : weightGroup.getValue()) {
                collectFirmsFromContinent(continent, groupFirms);
            }

            Collections.shuffle(groupFirms);
            result.addAll(groupFirms);
        }

        // Mundial firms — weight 0, always last
        List<Site> mundialFirms = new ArrayList<>();
        collectFilteredFirms(FirmsBuilder.getMundial(), mundialFirms);
        Collections.shuffle(mundialFirms);
        result.addAll(mundialFirms);

        return result;
    }

    private static void collectFirmsFromContinent(String continent, List<Site> dest) {
        Supplier<Site[]> getter = BY_CONTINENT_GETTERS.get(continent);
        if (getter != null) collectFilteredFirms(getter.get(), dest);
    }

    private static void collectFilteredFirms(Site[] firms, List<Site> dest) {
        for (Site site : firms) {
            if (site != null
                    && !FirmsOMonth.isFirmRegisteredInMonth(site.name)
                    && !FirmsExhausted.isFirmExhausted(site.name)) {
                dest.add(site);
            }
        }
    }


    /**
     * Shows continent configuration with firms breakdown.
     */
    private static void showContinentBreakdown() {
        int lineLength = 80;
        String title = "| CONTINENT CONFIGURATION |";
        int padding = (lineLength - title.length()) / 2;

        System.out.println("\n" + "=".repeat(lineLength));
        System.out.println(" ".repeat(padding) + title);
        System.out.println("=".repeat(lineLength));

        // Header
        System.out.printf(" %-18s │ %6s │ %6s │ %12s │ %12s%n",
                "Continent", "Status", "Weight", "Total Firms", "Max Lawyers");
        System.out.println("-".repeat(lineLength));

        Object[][] continents = {
                {"Africa",   FirmsBuilder.getAfrica()},
                {"Asia",     FirmsBuilder.getAsia()},
                {"Europe",   FirmsBuilder.getEurope()},
                {"Americas", FirmsBuilder.getAmericas()},
                {"Oceania",  FirmsBuilder.getOceania()},
        };

        int totalEnabled = 0, totalDisabled = 0;
        int firmsEnabled = 0, firmsDisabled = 0;
        int lawyersEnabled = 0, lawyersDisabled = 0;

        for (Object[] continent : continents) {
            String name  = (String) continent[0];
            Site[] firms = (Site[]) continent[1];

            boolean enabled    = ContinentConfig.isContinentEnabled(name);
            int weight         = ContinentConfig.getContinentWeight(name);
            int totalFirms     = firms.length;
            int maxLawyers     = countTotalMaxLawyer(firms);

            String statusIcon = enabled ? GREEN + "ON " + RESET : RED + "OFF" + RESET;
            String lineColor  = enabled ? "" : DIM;
            String endColor   = enabled ? "" : RESET;

            System.out.printf("%s %-18s │ %s   │ %6d │ %12d │ %12d%s%n",
                    lineColor, name, statusIcon, weight, totalFirms, maxLawyers, endColor);

            if (enabled) {
                totalEnabled++;
                firmsEnabled += totalFirms;
                lawyersEnabled += maxLawyers;
            } else {
                totalDisabled++;
                firmsDisabled += totalFirms;
                lawyersDisabled += maxLawyers;
            }
        }

        // Mundial (always enabled, weight 0)
        Site[] mundial      = FirmsBuilder.getMundial();
        int mundialTotal    = mundial.length;
        int mundialLawyers  = countTotalMaxLawyer(mundial);

        System.out.println("-".repeat(lineLength));
        System.out.printf(" %-18s │ %s%s%s   │ %6d │ %12d │ %12d%n",
                "Mundial", CYAN, "***", RESET, 0, mundialTotal, mundialLawyers);

        // Summary
        System.out.println("=".repeat(lineLength));

        int grandTotalFirms   = firmsEnabled + firmsDisabled + mundialTotal;
        int grandTotalLawyers = lawyersEnabled + lawyersDisabled + mundialLawyers;
        int activeFirms       = firmsEnabled + mundialTotal;
        int activeLawyers     = lawyersEnabled + mundialLawyers;

        System.out.printf("%n %sSUMMARY:%s%n", BOLD, RESET);
        System.out.printf("   Continents: %s%d enabled%s / %s%d disabled%s%n",
                GREEN, totalEnabled, RESET, RED, totalDisabled, RESET);
        System.out.printf("   Active Firms:    %s%d%s / %d total  (%s%.1f%%%s)%n",
                GREEN, activeFirms, RESET, grandTotalFirms, YELLOW, (activeFirms * 100.0 / grandTotalFirms), RESET);
        System.out.printf("   Active Lawyers:  %s%d%s / %d total  (%s%.1f%%%s)%n",
                GREEN, activeLawyers, RESET, grandTotalLawyers, YELLOW, (activeLawyers * 100.0 / grandTotalLawyers), RESET);

        System.out.println("=".repeat(lineLength));
    }


    /**
     * A log print to count all active sites by continent (only enabled continents)
     */
    private static int showSitesCompleted() {
        int lineLength = 90;
        String title = "| ACTIVE SITES |";
        int padding = (lineLength - title.length()) / 2;

        System.out.println("\n" + "-".repeat(padding) + title + "-".repeat(padding));

        Object[][] categories = {
                { "Africa",   FirmsBuilder.getAfrica()   },
                { "Asia",     FirmsBuilder.getAsia()     },
                { "Europe",   FirmsBuilder.getEurope()   },
                { "Americas", FirmsBuilder.getAmericas() },
                { "Oceania",  FirmsBuilder.getOceania()  },
                { "Mundial",  FirmsBuilder.getMundial()  },
        };

        int grandTotal = 0;
        int totalFirmsActive = 0;

        for (Object[] category : categories) {
            String label = (String) category[0];
            Site[] firms = (Site[]) category[1];

            // Skip disabled continents (Mundial is always shown)
            if (!label.equals("Mundial") && !ContinentConfig.isContinentEnabled(label)) continue;

            int totalToRegister = countTotalMaxLawyer(firms);
            grandTotal += totalToRegister;
            totalFirmsActive += firms.length;

            System.out.printf(" - %-10s %s%-30s%s To Register: %s%d%s%n",
                    label + ":", YELLOW, firms.length + " firms", RESET, BLUE, totalToRegister, RESET);
        }

        System.out.println("-".repeat(lineLength));
        System.out.printf("  %sTotal Active Firms:%s %s%-15d%s %sMax Lawyers:%s %s%d%s%n",
                BOLD, RESET, YELLOW, totalFirmsActive, RESET, BOLD, RESET, BLUE, grandTotal, RESET);
        System.out.println("-".repeat(lineLength));

        return grandTotal;
    }


    private static int countTotalMaxLawyer(Site[] firms) {
        int total = 0;
        for (Site firm : firms) {
            total += firm.maxLawyersForSite;
        }
        return total;
    }

    /**
     * A log print to count all filtered lawyers
     */
    private static int showFilteredContacts() {
        ContactsAlreadyRegisteredSheet sheet = new ContactsAlreadyRegisteredSheet();
        int lastRow = sheet.getSheet().getLastRowNum();
        int nonEmptyRows = 0;

        for (int i = 0; i <= lastRow; i++) {
            Row row = sheet.getSheet().getRow(i);
            if (row == null) continue;

            for (Cell cell : row) {
                if (cell.getCellType() != CellType.BLANK && cell.getCellType() != CellType._NONE) {
                    nonEmptyRows++;
                    break;
                }
            }
        }

        int lineLength = 90;
        String title = "| FILTERED LAWYERS |";
        int padding = Math.max(0, (lineLength - title.length()) / 2);

        System.out.println("-".repeat(padding) + title + "-".repeat(lineLength - padding - title.length()));
        System.out.printf(" - Filtered Lawyers: %s%d%s%n", BLUE, nonEmptyRows, RESET);
        System.out.println("-".repeat(lineLength));

        return nonEmptyRows;
    }


    /**
     * Perform a log of the total of lawyers registered by the Search in Web and by the Filtered Contacts file.
     * Then it shows the total amount of lawyers registered
     */
    private static void showAllFirmsCompleted() {
        System.out.println("\n");

        // Show continent breakdown first
        showContinentBreakdown();

        // Then show active sites and filtered contacts
        int totalMaxLawyers = showFilteredContacts();
        totalMaxLawyers += showSitesCompleted();

        int lineLength = 90;
        String title = "| GRAND TOTAL |";
        int padding = Math.max(0, (lineLength - title.length()) / 2);

        System.out.println("=".repeat(padding) + title + "=".repeat(lineLength - padding - title.length()));
        System.out.printf(" %sTotal Lawyers Available:%s %s%d%s%n", BOLD, RESET, BLUE, totalMaxLawyers, RESET);
        System.out.println("=".repeat(lineLength));
    }


    // ==================== SNAPSHOT ====================

    private static final ObjectMapper snapshotMapper = new ObjectMapper();
    private static final String[] CONTINENT_NAMES = {
            "Africa", "Asia", "Europe", "Americas", "Oceania"
    };

    /**
     * Collects the current state of all continents + Mundial into a Map structure.
     */
    private static Map<String, Object> collectCurrentState() {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Map<String, Map<String, Integer>> continents = new LinkedHashMap<>();
        for (String name : CONTINENT_NAMES) {
            Supplier<Site[]> getter = BY_CONTINENT_GETTERS.get(name);
            Site[] firms = getter != null ? getter.get() : new Site[0];

            continents.put(name, Map.of(
                    "total",      firms.length,
                    "maxLawyers", countTotalMaxLawyer(firms)
            ));
        }
        snapshot.put("continents", continents);

        Site[] mundial = FirmsBuilder.getMundial();
        snapshot.put("mundial", Map.of(
                "total",      mundial.length,
                "maxLawyers", countTotalMaxLawyer(mundial)
        ));

        return snapshot;
    }

    /**
     * Saves the current system state to a JSON snapshot file.
     */
    private static void saveSnapshot() {
        try {
            Map<String, Object> snapshot = collectCurrentState();
            snapshotMapper.writerWithDefaultPrettyPrinter().writeValue(new File(CONFIG.SYSTEM_SNAPSHOT_FILE), snapshot);
            System.out.println("\n " + GREEN + "Snapshot salvo com sucesso." + RESET);
        } catch (IOException e) {
            System.err.println("Error saving snapshot: " + e.getMessage());
        }
    }

    /**
     * Loads the previous snapshot from the JSON file.
     * @return the snapshot map, or null if no snapshot exists
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> loadSnapshot() {
        Path path = Path.of(CONFIG.SYSTEM_SNAPSHOT_FILE);
        if (!Files.exists(path)) return null;

        try {
            String json = Files.readString(path);
            return snapshotMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            System.err.println("Error reading snapshot: " + e.getMessage());
            return null;
        }
    }

    /**
     * Builds a right-aligned cell of exactly {@code width} visible characters.
     * When current != previous the delta is appended in color.
     * ANSI codes are excluded from the width calculation so columns stay aligned.
     */
    private static String formatCell(int current, int previous, int width) {
        int delta = current - previous;

        String visible;   // what the user will actually see
        String colored;   // same string but with ANSI color on the delta part

        if (delta > 0) {
            String d = "(+" + delta + ")";
            visible = current + " " + d;
            colored = current + " " + GREEN + d + RESET;
        } else if (delta < 0) {
            String d = "(" + delta + ")";
            visible = current + " " + d;
            colored = current + " " + RED + d + RESET;
        } else {
            visible = String.valueOf(current);
            colored = visible;
        }

        int pad = Math.max(0, width - visible.length());
        return " ".repeat(pad) + colored;
    }

    /** Plain right-aligned cell (no comparison). */
    private static String formatCell(int current, int width) {
        String s = String.valueOf(current);
        int pad = Math.max(0, width - s.length());
        return " ".repeat(pad) + s;
    }

    /**
     * Shows a comparison between the current system state and the last saved snapshot.
     */
    @SuppressWarnings("unchecked")
    private static void showSystemState() {
        Map<String, Object> previous = loadSnapshot();

        if (previous == null) {
            System.out.println("\n " + YELLOW + "Nenhum snapshot anterior encontrado." + RESET);
            System.out.println(" Execute qualquer opcao e saia para gerar o primeiro snapshot.\n");
            return;
        }

        Map<String, Map<String, Object>> prevContinents = (Map<String, Map<String, Object>>) previous.get("continents");
        Map<String, Object> prevMundial = (Map<String, Object>) previous.get("mundial");
        String prevTimestamp = (String) previous.get("timestamp");

        final int COL  = 14;
        final int LINE = 1 + 18 + 3 + (COL + 3) * 2;

        String title = "| SYSTEM STATE COMPARISON |";
        int padding = (LINE - title.length()) / 2;

        System.out.println("\n" + "=".repeat(LINE));
        System.out.println(" ".repeat(Math.max(0, padding)) + title);
        System.out.println("=".repeat(LINE));

        // Header — plain %Ns works here because there are no ANSI codes
        System.out.printf(" %-18s │ %"+COL+"s │ %"+COL+"s%n",
                "Continent", "Total Firms", "Max Lawyers");
        System.out.println("-".repeat(LINE));

        for (String name : CONTINENT_NAMES) {
            Supplier<Site[]> getter = BY_CONTINENT_GETTERS.get(name);
            Site[] firms = getter != null ? getter.get() : new Site[0];

            int curTotal   = firms.length;
            int curLawyers = countTotalMaxLawyer(firms);

            Map<String, Object> prev = prevContinents != null ? prevContinents.get(name) : null;

            if (prev != null) {
                // Backward-compat: old snapshots had "byPage"+"byNewPage"; new ones have "total"
                int prevTotal = prev.containsKey("total")
                        ? (int) prev.get("total")
                        : ((int) prev.getOrDefault("byPage", 0) + (int) prev.getOrDefault("byNewPage", 0));
                int prevLawyers = (int) prev.getOrDefault("maxLawyers", 0);

                System.out.printf(" %-18s │ %s │ %s%n",
                        name,
                        formatCell(curTotal,   prevTotal,   COL),
                        formatCell(curLawyers, prevLawyers, COL));
            } else {
                System.out.printf(" %-18s │ %s │ %s%n",
                        name,
                        formatCell(curTotal,   COL),
                        formatCell(curLawyers, COL));
            }
        }

        // Mundial row
        Site[] mundial  = FirmsBuilder.getMundial();
        int curMTotal   = mundial.length;
        int curMLawyers = countTotalMaxLawyer(mundial);

        System.out.println("-".repeat(LINE));
        if (prevMundial != null) {
            int prevMTotal = prevMundial.containsKey("total")
                    ? (int) prevMundial.get("total")
                    : ((int) prevMundial.getOrDefault("byPage", 0) + (int) prevMundial.getOrDefault("byNewPage", 0));
            int prevMLawyers = (int) prevMundial.getOrDefault("maxLawyers", 0);

            System.out.printf(" %-18s │ %s │ %s%n",
                    "Mundial",
                    formatCell(curMTotal,   prevMTotal,   COL),
                    formatCell(curMLawyers, prevMLawyers, COL));
        } else {
            System.out.printf(" %-18s │ %s │ %s%n",
                    "Mundial",
                    formatCell(curMTotal,   COL),
                    formatCell(curMLawyers, COL));
        }

        System.out.println("=".repeat(LINE));
        String formattedTimestamp = LocalDateTime.parse(prevTimestamp)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        System.out.printf(" Last snapshot: %s%s%s%n", DIM, formattedTimestamp, RESET);
        System.out.println("=".repeat(LINE));
    }


    // ==================== AVAILABLE FIRMS ====================

    private static int countAvailable(Site[] firms) {
        int count = 0;
        for (Site site : firms) {
            if (site != null
                    && !FirmsOMonth.isFirmRegisteredInMonth(site.name)
                    && !FirmsExhausted.isFirmExhausted(site.name)) count++;
        }
        return count;
    }

    private static int countAvailableLawyers(Site[] firms) {
        int total = 0;
        for (Site site : firms) {
            if (site != null
                    && !FirmsOMonth.isFirmRegisteredInMonth(site.name)
                    && !FirmsExhausted.isFirmExhausted(site.name))
                total += site.maxLawyersForSite;
        }
        return total;
    }

    /**
     * Shows firms still available for future executions, broken down by active continents
     * (excluding firms already in monthFirms.txt).
     */
    private static void showAvailableFirms() {
        int lineLength = 80;

        String title1 = "| FIRMAS DISPONÍVEIS — CONTINENTES ATIVOS |";
        int pad1 = (lineLength - title1.length()) / 2;
        System.out.println("\n" + "=".repeat(lineLength));
        System.out.println(" ".repeat(Math.max(0, pad1)) + title1);
        System.out.println("=".repeat(lineLength));

        System.out.printf(" %-18s │ %12s │ %14s%n",
                "Continente", "Disponíveis", "Max Lawyers");
        System.out.println("-".repeat(lineLength));

        Object[][] allContinents = {
                {"Africa",   FirmsBuilder.getAfrica()},
                {"Asia",     FirmsBuilder.getAsia()},
                {"Europe",   FirmsBuilder.getEurope()},
                {"Americas", FirmsBuilder.getAmericas()},
                {"Oceania",  FirmsBuilder.getOceania()},
        };

        int activeAvailFirms   = 0;
        int activeAvailLawyers = 0;

        for (Object[] row : allContinents) {
            String name  = (String) row[0];
            Site[] firms = (Site[]) row[1];

            if (!ContinentConfig.isContinentEnabled(name)) continue;

            int avTot = countAvailable(firms);
            int avLaw = countAvailableLawyers(firms);

            activeAvailFirms   += avTot;
            activeAvailLawyers += avLaw;

            System.out.printf(" %-18s │ %12d │ %14d%n", name, avTot, avLaw);
        }

        // Mundial (always active)
        Site[] mundial = FirmsBuilder.getMundial();
        int mAvTot = countAvailable(mundial);
        int mAvLaw = countAvailableLawyers(mundial);

        activeAvailFirms   += mAvTot;
        activeAvailLawyers += mAvLaw;

        System.out.println("-".repeat(lineLength));
        System.out.printf(" %-18s │ %12d │ %14d%n", "Mundial", mAvTot, mAvLaw);
        System.out.println("=".repeat(lineLength));
        System.out.printf(" %sTOTAL (ativos):%s  %s%d firmas%s disponíveis  |  %s%d advogados%s à registrar%n",
                BOLD, RESET, GREEN, activeAvailFirms, RESET, BLUE, activeAvailLawyers, RESET);
        System.out.println("=".repeat(lineLength));
    }


    // ==================== EXHAUSTED FIRMS ====================

    private static void showExhaustedFirms() {
        int lineLength = 80;

        String title = "| FIRMAS ESGOTADAS |";
        int pad = (lineLength - title.length()) / 2;
        System.out.println("\n" + "=".repeat(lineLength));
        System.out.println(" ".repeat(Math.max(0, pad)) + title);
        System.out.println("=".repeat(lineLength));

        Path path = Path.of(CONFIG.EXHAUSTED_FIRMS_FILE);
        List<String> names;
        try {
            if (!Files.exists(path)) {
                names = List.of();
            } else {
                names = Files.readAllLines(path).stream()
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de firmas esgotadas: " + e.getMessage());
            return;
        }

        if (names.isEmpty()) {
            System.out.printf(" %sNenhuma firma esgotada registrada.%s%n", GREEN, RESET);
        } else {
            for (int i = 0; i < names.size(); i++) {
                System.out.printf(" %2d. %s%s%s%n", i + 1, RED, names.get(i), RESET);
            }
        }

        System.out.println("=".repeat(lineLength));
        System.out.printf(" %sTotal esgotadas:%s %s%d%s%n",
                BOLD, RESET, RED, names.size(), RESET, DIM, RESET);
        System.out.println("=".repeat(lineLength));
    }
}

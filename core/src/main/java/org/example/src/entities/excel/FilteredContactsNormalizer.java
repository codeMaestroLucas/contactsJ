package org.example.src.entities.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.example.src.CONFIG;
import org.example.src.utils.TreatLawyerParams;

import java.util.*;

public final class FilteredContactsNormalizer extends Excel {

    public FilteredContactsNormalizer(String filePath) {
        super(filePath);
    }

    public void normalize() {
        int lastRowNum = this.getSheet().getLastRowNum();

        for (int i = 1; i <= lastRowNum; i++) {
            Row row = this.getSheet().getRow(i);
            if (row == null) continue;

            setCell(row, 4,  TreatLawyerParams.treatName(getCellValue(row.getCell(4))));
            setCell(row, 5,  TreatLawyerParams.treatEmail(getCellValue(row.getCell(5))));
            setCell(row, 6,  TreatLawyerParams.treatPhone(getCellValue(row.getCell(6))));
            setCell(row, 7,  TreatLawyerParams.treatCountry(getCellValue(row.getCell(7))));
            setCell(row, 8,  TreatLawyerParams.treatPracticeArea(getCellValue(row.getCell(8))));
            setCell(row, 9,  getCellValue(row.getCell(9)).trim());
            setCell(row, 12, TreatLawyerParams.treatRole(getCellValue(row.getCell(12))));
            setCell(row, 13, getCellValue(row.getCell(13)).trim());
        }

        normalizeFirmNames();
        this.saveSheet();
        this.sort();
    }


    private void normalizeFirmNames() {
        org.apache.poi.ss.usermodel.Sheet sheet = this.getSheet();
        int lastRowNum = sheet.getLastRowNum();

        Map<String, Integer> firmCount       = new LinkedHashMap<>();
        Map<String, String>  firmEmailDomain = new HashMap<>();
        Map<String, String>  firmLinkDomain  = new HashMap<>();

        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String firm = getCellValue(row.getCell(13));
            if (firm.isEmpty()) continue;

            firmCount.merge(firm, 1, Integer::sum);

            if (!firmEmailDomain.containsKey(firm)) {
                String email  = getCellValue(row.getCell(5));
                int    atIdx  = email.indexOf('@');
                if (atIdx >= 0) {
                    String domain = email.substring(atIdx + 1).toLowerCase().trim();
                    if (!isGenericEmailProvider(domain))
                        firmEmailDomain.put(firm, domain);
                }
            }

            if (!firmLinkDomain.containsKey(firm)) {
                String link   = getCellValue(row.getCell(9));
                String domain = extractDomain(link);
                if (!domain.isEmpty())
                    firmLinkDomain.put(firm, domain);
            }
        }

        List<String>        firms  = new ArrayList<>(firmCount.keySet());
        Map<String, String> parent = new HashMap<>();
        for (String f : firms) parent.put(f, f);

        for (int i = 0; i < firms.size(); i++) {
            for (int j = i + 1; j < firms.size(); j++) {
                String a = firms.get(i), b = firms.get(j);
                if (areSameFirm(a, b, firmEmailDomain, firmLinkDomain))
                    union(parent, a, b);
            }
        }

        Map<String, List<String>> groups = new HashMap<>();
        for (String f : firms)
            groups.computeIfAbsent(find(parent, f), k -> new ArrayList<>()).add(f);

        Map<String, String> firmToCanonical = new HashMap<>();
        for (List<String> group : groups.values()) {
            String canonical = group.stream()
                    .max(Comparator.comparingInt((String f) -> firmCount.getOrDefault(f, 0))
                            .thenComparingInt(String::length))
                    .orElse(group.get(0));
            for (String f : group) firmToCanonical.put(f, canonical);
        }

        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String firm = getCellValue(row.getCell(13));
            if (firm.isEmpty()) continue;
            String canonical = firmToCanonical.get(firm);
            if (canonical != null && !canonical.equals(firm))
                setCell(row, 13, canonical);
        }
    }

    private static boolean areSameFirm(String a, String b,
                                       Map<String, String> emailDomains,
                                       Map<String, String> linkDomains) {
        String emailA = emailDomains.get(a), emailB = emailDomains.get(b);
        if (emailA != null && emailB != null && emailA.equals(emailB)) return true;

        String linkA = linkDomains.get(a), linkB = linkDomains.get(b);
        if (linkA != null && linkB != null && linkA.equals(linkB)) return true;

        String normA = normalizeFirmName(a), normB = normalizeFirmName(b);
        if (normA.length() >= 3 && normB.length() >= 3) {
            if (normA.contains(normB) || normB.contains(normA)) return true;
        }

        return false;
    }

    private static String normalizeFirmName(String name) {
        return TreatLawyerParams.removeAccents(name)
                .toLowerCase()
                .replaceAll("\\b(abogados|lawyers|law|legal|attorneys|advocaten|advogados|avocats"
                        + "|advocats|advokater|rechtsanwalte|advokati|partners|associates"
                        + "|llp|llc|plc|lp|and|&)\\b", "")
                .replaceAll("[^a-z0-9 ]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static String extractDomain(String url) {
        if (url == null || url.isBlank()) return "";
        String domain = url.replaceAll("(?i)https?://", "").replaceAll("(?i)^www\\.", "");
        int slash = domain.indexOf('/');
        if (slash > 0) domain = domain.substring(0, slash);
        return domain.toLowerCase().trim();
    }

    private static boolean isGenericEmailProvider(String domain) {
        return domain.startsWith("gmail.") || domain.startsWith("outlook.")
                || domain.startsWith("hotmail.") || domain.startsWith("yahoo.")
                || domain.startsWith("icloud.") || domain.startsWith("live.");
    }

    private static String find(Map<String, String> parent, String x) {
        if (!parent.get(x).equals(x)) parent.put(x, find(parent, parent.get(x)));
        return parent.get(x);
    }

    private static void union(Map<String, String> parent, String a, String b) {
        parent.put(find(parent, a), find(parent, b));
    }

    // N (Firm, col 13) → H (Country, col 7)
    private void sort() {
        super.sortRows(new int[]{13, 7}, 14);
    }

    private static void setCell(Row row, int colIndex, String value) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) cell = row.createCell(colIndex);
        cell.setCellValue(value);
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();

            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                yield Double.toString(cell.getNumericCellValue());
            }

            case BOOLEAN -> Boolean.toString(cell.getBooleanCellValue());

            case FORMULA -> cell.getCellFormula();

            default -> "";
        };
    }

    public static void main(String[] args) {
        FilteredContactsNormalizer normalizer = new FilteredContactsNormalizer(CONFIG.FILTERED_ACTIVE_CONTACTS_FILE);
        normalizer.normalize();
        System.out.println("Normalization complete.");
    }
}

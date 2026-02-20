package org.example.src;

public class CONFIG {
    // FILE PATHS
    public static String SHEET_FILE = "src/main/resources/baseFiles/excel/Sheet.xlsx";
    public static String CONTACTS_FILE = "src/main/resources/baseFiles/excel/Contacts.xlsx";
    public static String REPORTS_FILE = "src/main/resources/baseFiles/excel/Reports.xlsx";
    public static String FILTERED_ACTIVE_CONTACTS_FILE = "src/main/resources/baseFiles/excel/filteredCollectedContacts.xlsx";
    public static String LAST_CHECK_SHEET_FILE = "src/main/resources/baseFiles/excel/LastCheckSheet.xlsx";
    public static String LAST_FIRM_REGISTERED_FILE = "src/main/resources/baseFiles/lastRowRegisteredInContacts.txt";

    public static String EMAILS_MONTH_FOLDER_FILE = "data/sites/";
    public static String EMAILS_TO_AVOID_FOLDER_FILE = "data/_toAvoid/";

    public static String BY_PAGE_FILE = "byPage/";
    public static String BY_NEW_PAGE_FILE = "byNewPage/";
    public static String MONTH_FILE = "data/monthFirms.txt";
    public static String SYSTEM_SNAPSHOT_FILE = "src/main/resources/baseFiles/json/systemSnapshot.json";

    // OTHERS
    public static int TOTAL_LAWYERS_TO_GET = 270;
    public static int LAWYERS_IN_FILTER = 70;

    public static int TIMEOUT_MINUTES = 4;
}

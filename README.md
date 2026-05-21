# Law Firm Web Scraper - Complete Documentation

> **Version:** 2.1
> **Last Updated:** April 2026
> **Platform:** Java 17+ with Selenium WebDriver

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [System Architecture](#2-system-architecture)
3. [Directory Structure](#3-directory-structure)
4. [Core Components](#4-core-components)
5. [Utility Classes Reference](#5-utility-classes-reference)
   - [5.7 VCard.java](#57-vcardjavanew)
6. [Configuration System](#6-configuration-system)
7. [Continent Configuration](#7-continent-configuration)
8. [Creating New Scrapers](#8-creating-new-scrapers)
9. [Data Flow & Execution](#9-data-flow--execution)
10. [Error Logging System](#10-error-logging-system)
11. [Code Generation Guidelines](#11-code-generation-guidelines)
12. [Configuration Files Reference](#12-configuration-files-reference)

---

## 1. Project Overview

### 1.1 Purpose

This application automates the collection of lawyer contact information from law firm websites worldwide. It uses **Selenium WebDriver** to control a Chrome browser, navigate web pages, extract data, and store it in **Excel files**.

### 1.2 Key Features

- **Multi-strategy scraping**: Supports both pagination-based (`ByPage`) and profile-based (`ByNewPage`) extraction
- **Continent-based organization**: Firms organized by geographic regions with enable/disable control
- **Data validation**: Automatic validation of emails, countries, duplicates
- **Error tracking**: Comprehensive logging system for maintenance
- **Rate limiting**: Timeout controls to prevent getting blocked
- **Excel integration**: Direct output to Excel spreadsheets

### 1.3 Technology Stack

| Technology | Purpose |
|------------|---------|
| Java 17+ | Core language |
| Selenium WebDriver | Browser automation |
| Apache POI | Excel file manipulation |
| Lombok | Boilerplate reduction |
| Chrome/ChromeDriver | Browser engine |

---

## 2. System Architecture

### 2.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                          Main.java                               │
│                    (Orchestration Layer)                         │
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
┌──────────────────┐ ┌──────────────┐ ┌──────────────────┐
│  CompletedFirms  │ │   Builders   │ │  ContinentConfig │
│  (Firm Manager)  │ │  (ByPage/    │ │  (Configuration) │
│                  │ │   ByNewPage) │ │                  │
└──────────────────┘ └──────────────┘ └──────────────────┘
              │               │               │
              └───────────────┼───────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Site (Abstract Base)                          │
│                 ┌─────────────┬─────────────┐                    │
│                 ▼             ▼             ▼                    │
│            ByPage        ByNewPage      SiteUtils                │
│         (Strategy 1)   (Strategy 2)    (Helpers)                 │
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
┌──────────────────┐ ┌──────────────┐ ┌──────────────────┐
│    Extractor     │ │  Validations │ │    ErrorLogger   │
│  (Data Extract)  │ │   (Filters)  │ │    (Tracking)    │
└──────────────────┘ └──────────────┘ └──────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Excel Layer                                 │
│ Sheet.java │ Reports.java │ ContactsAlreadyRegisteredSheet.java  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Design Patterns Used

| Pattern | Implementation | Purpose |
|---------|----------------|---------|
| **Singleton** | `MyDriver`, `ErrorLogger`, `Extractor`, `SiteUtils` | Single instance management |
| **Template Method** | `Site` → `ByPage`/`ByNewPage` | Define scraping algorithm structure |
| **Strategy** | `ByPage` vs `ByNewPage` | Different scraping approaches |
| **Builder** | `Lawyer.builder()`, `FirmsBuilder` | Object construction |

### 2.3 Scraping Strategies

#### ByPage Strategy
- All lawyer data visible on listing pages
- Pagination handled by page index
- No need to open individual profiles
- Faster but requires all data on listing page

#### ByNewPage Strategy
- Must open each lawyer's profile page in a new tab
- Extracts detailed information from profile
- Slower but gets more comprehensive data
- Automatically closes tabs after extraction

---

## 3. Directory Structure

O projeto é um Maven multi-módulo com dois módulos principais: **`core`** (infraestrutura compartilhada) e **`firms`** (scrapers de produção e ponto de entrada).

```
project-root/
├── pom.xml
├── README.md
├── mermaidStructure.txt
├── data/                                       # runtime — gerado durante execução
│   ├── monthFirms.txt                          # firmas processadas no mês
│   ├── exhaustedFirms.txt                      # firmas sem mais contatos disponíveis
│   ├── sites/
│   │   ├── byPage/<FirmName>.txt               # e-mails coletados no mês (ByPage)
│   │   └── byNewPage/<FirmName>.txt            # e-mails coletados no mês (ByNewPage)
│   └── _toAvoid/
│       ├── byPage/<FirmName>.txt               # e-mails permanentemente evitados
│       └── byNewPage/<FirmName>.txt
│
├── core/                                       # módulo base (infraestrutura compartilhada)
│   └── src/main/
│       ├── java/org/example/
│       │   ├── exceptions/
│       │   │   ├── LawyerExceptions.java       # erros de extração de dados
│       │   │   └── ValidationExceptions.java   # erros de validação
│       │   │
│       │   └── src/
│       │       ├── CONFIG.java                 # constantes globais e paths
│       │       │
│       │       ├── entities/
│       │       │   ├── Lawyer.java             # modelo de dados
│       │       │   ├── MyDriver.java           # WebDriver singleton
│       │       │   │
│       │       │   ├── BaseSites/
│       │       │   │   ├── Site.java           # classe base abstrata
│       │       │   │   ├── ByPage.java         # estratégia de paginação por URL
│       │       │   │   ├── ByNewPage.java      # estratégia de perfil em nova aba
│       │       │   │   └── SiteUtils.java      # helpers de DOM
│       │       │   │
│       │       │   └── excel/
│       │       │       ├── Excel.java                          # base Excel (Apache POI)
│       │       │       ├── Sheet.java                          # planilha principal de saída
│       │       │       ├── Reports.java                        # métricas de execução
│       │       │       ├── ContactsAlreadyRegisteredSheet.java # leitura do filteredCollectedContacts.xlsx
│       │       │       ├── FilteredContactsNormalizer.java     # normalização de dados do xlsx filtrado
│       │       │       └── LastCheck.java                      # validação de duplicatas via EmailDuplicateChecker
│       │       │
│       │       ├── sites/
│       │       │   └── to_test/                # scrapers em desenvolvimento / teste
│       │       │       ├── americas/
│       │       │       ├── asia/
│       │       │       ├── ...
│       │       │       └── _standingBy/        # firmas pausadas (país evitado, etc.)
│       │       │
│       │       └── utils/
│       │           ├── ContinentConfig.java    # leitor de continentsConfig.json
│       │           ├── EmailOfMonth.java       # rastreador de e-mails do mês
│       │           ├── ErrorLogger.java        # sistema de log de erros
│       │           ├── Extractor.java          # extração de dados do DOM
│       │           ├── FirmsExhausted.java     # rastreador de firmas esgotadas
│       │           ├── FirmsOMonth.java        # rastreador de firmas do mês
│       │           ├── NoSleep.java            # impede suspensão do sistema (caffeinate/JNA)
│       │           ├── Stopwatch.java          # temporizador de execução
│       │           ├── TreatLawyerParams.java  # sanitização e normalização de dados
│       │           ├── VCard.java              # download e parse de arquivos .vcf
│       │           ├── Validations.java        # regras de validação
│       │           │
│       │           ├── myInterface/
│       │           │   └── MyInterfaceUtls.java
│       │           │
│       │           ├── python/
│       │           │   ├── generate_docs.py    # empacota docs em .zip para contexto de chat
│       │           │   ├── check_todos.py      # verifica status das firmas nas filas todos/
│       │           │   └── move_firms.py       # move classes de to_test para produção
│       │           │
│       │           └── validation/
│       │               └── EmailDuplicateChecker.java  # verificação de duplicatas via Google Sheets
│       │
│       └── resources/baseFiles/
│           ├── lastRowRegisteredInContacts.txt
│           ├── excel/
│           │   ├── Sheet.xlsx
│           │   ├── Reports.xlsx
│           │   ├── filteredCollectedContacts.xlsx
│           │   ├── LastCheckSheet.xlsx
│           │   └── RawXL/                      # templates originais (backup)
│           │       ├── Sheet.xlsx
│           │       ├── Reports.xlsx
│           │       └── filteredCollectedContacts.xlsx
│           │
│           ├── json/
│           │   ├── continentsConfig.json
│           │   ├── countriesToAvoidPermanent.json
│           │   ├── countriesToAvoidTemporary.json
│           │   ├── countryAliases.json
│           │   ├── firmsToAvoid.json
│           │   ├── practiceAreas.json
│           │   └── systemSnapshot.json
│           │
│           ├── todos/
│           │   ├── byNewPage.json              # fila de firmas ByNewPage a implementar
│           │   ├── byPage.json                 # fila de firmas ByPage a implementar
│           │   └── uncategorized.json          # firmas sem estratégia definida
│           │
│           └── instructions/                   # prompts e convenções para geração de código
│               └── *.md / *.txt
│
└── firms/                                      # módulo de scrapers de produção
    └── src/
        ├── main/java/org/example/
        │   ├── Main.java                       # ponto de entrada da aplicação
        │   │
        │   └── src/
        │       ├── sites/                      # ~1560 classes, organizadas por continente
        │       │   ├── africa/                 # ~89 classes
        │       │   ├── americas/               # ~354 classes
        │       │   ├── asia/                   # ~325 classes
        │       │   ├── europe/                 # ~595 classes
        │       │   ├── mundial/                # ~136 classes
        │       │   └── oceania/                # ~61 classes
        │       │
        │       └── utils/myInterface/
        │           ├── CompletedFirms.java     # menu CLI + construção da lista de firmas
        │           └── FirmsBuilder.java       # discovery automático via reflection
        │
        └── test/java/entities/BaseSites/
            ├── TestSite.java                   # executa scraper isolado sem registrar dados
            └── BatchTestRunner.java            # executa múltiplos scrapers em sequência
```

---

## 4. Core Components

### 4.1 Site.java (Abstract Base Class)

The foundation class for all scrapers. Defines common structure and behavior.

```java
public abstract class Site {
    // Core properties
    public final String name;
    protected final String link;
    public int lawyersRegistered;
    protected final int totalPages;
    public final int maxLawyersForSite;

    // Shared instances
    protected final WebDriver driver;
    protected final SiteUtils siteUtl;
    protected final Extractor extractor;
    protected ErrorLogger errorLogger;

    // Abstract methods - must be implemented
    protected abstract List<WebElement> getLawyersInPage();
    protected abstract Object getLawyer(WebElement lawyer) throws Exception;
    public abstract Runnable searchForLawyers(boolean showLogs);
}
```

**Key Methods:**

| Method | Purpose |
|--------|---------|
| `addLawyer(Lawyer)` | Registers a validated lawyer to the sheet |
| `registerValidLawyer(Object, int, int, boolean)` | Validates and registers lawyer data |
| `getSocials(List<WebElement>, boolean)` | Extracts email/phone from social links |

### 4.2 ByPage.java

Strategy for sites where all lawyer data is visible on listing pages.

```java
public abstract class ByPage extends Site {
    // Constructor
    protected ByPage(String name, String link, int totalPages, int maxLawyersForSite);

    // Template method - defines scraping algorithm
    public Runnable searchForLawyers(boolean showLogs) {
        // 1. Mark firm as being processed
        errorLogger.startFirm(this.name);

        // 2. Iterate through pages
        for (int i = 0; i < totalPages; i++) {
            accessPage(i);
            List<WebElement> lawyers = getLawyersInPage();

            // 3. Process each lawyer
            for (WebElement lawyer : lawyers) {
                Object details = getLawyer(lawyer);
                registerValidLawyer(details, ...);
            }
        }
    }

    // Must implement
    protected abstract void accessPage(int index) throws InterruptedException;
}
```

### 4.3 ByNewPage.java

Strategy for sites requiring navigation to individual profile pages.

```java
public abstract class ByNewPage extends Site {
    // Same constructor as ByPage

    // Additional abstract method
    public abstract String openNewTab(WebElement lawyer) throws LawyerExceptions;

    // Key difference: opens new tab and closes it after extraction
    public Runnable searchForLawyers(boolean showLogs) {
        // Similar to ByPage but with tab management
        for (WebElement lawyer : lawyers) {
            try {
                Object details = getLawyer(lawyer);
                registerValidLawyer(details, ...);
            } finally {
                // Automatically close tab if multiple tabs open
                if (driver.getWindowHandles().size() > 1) {
                    MyDriver.closeCurrentTab();
                }
            }
        }
    }
}
```

NOTE: If the lawyer's role is present in the file, the filter should be applied at this point.
```java
public abstract class ByNewPage extends Site {
    // Same constructor as ByPage

   private final By[] byRoleArray = {
           <selector>,
           <selector>
   };

    @Override
    protected List<WebElement> getLawyersInPage() {
       String[] validRoles = {"partner", "counsel", "senior associate"};

       try {
          WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
          List<WebElement> lawyers = wait.until(
                  ExpectedConditions.presenceOfAllElementsLocatedBy(
                          <selector>
                  )
          );
          return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
       } catch (Exception e) {
          return List.of();
       }
    }
}
```

### 4.4 Lawyer.java (Data Model)

```java
@Data
public final class Lawyer {
    public String link;
    public String name;
    public String role;
    public String firm;
    public String country;
    public String practiceArea;
    public String email;
    public String phone;
    public String specialism;

    @Builder
    public Lawyer(...) {
        // Auto-treatment of all fields
        this.email = treatEmail(email);
        this.name = name.isEmpty() ? getNameFromEmail(this.email) : treatName(name);
        this.role = treatRole(role);
        // ... etc
    }
}
```

### 4.5 MyDriver.java (WebDriver Singleton)

```java
public final class MyDriver {
    private static WebDriver driver;

    public static WebDriver getINSTANCE();

    // Configuration (must be called before getINSTANCE())
    public static void setHeadless(boolean value);  // default: false

    // Navigation methods
    public static void waitForPageToLoad();
    public static void rollDown(int times, double sleepTime);         // scroll N times by innerHeight
    public static void rollDownToBottom(double sleepTime);            // scroll by innerHeight until page stops growing
    public static void scrollToBottom(double sleepTime);              // jump to scrollHeight until page stops growing
    public static void openNewTab(String url);
    public static void closeCurrentTab();
    public static void switchToTab(int index);

    // Click methods
    public static void clickOnElement(By by);
    public static void clickOnElement(WebElement element);
    public static void clickOnAddBtn(Object button);
    public static void clickOnElementMultipleTimes(Object element, int times, double sleep);

    // Lifecycle
    public static void quitDriver();
}
```

---

## 5. Utility Classes Reference

### 5.1 Extractor.java

Robust data extraction with exception handling.

```java
public class Extractor {
    /**
     * Extract text content from an element
     * @param lawyer - Parent WebElement
     * @param locators - Array of By locators to find target
     * @param fieldName - Field name for error messages
     * @param exceptionSupplier - Function to create appropriate exception
     */
    public String extractLawyerText(
        WebElement lawyer,
        By[] locators,
        String fieldName,
        Function<String, LawyerExceptions> exceptionSupplier
    ) throws LawyerExceptions;

    /**
     * Extract attribute value from an element
     */
    public String extractLawyerAttribute(
        WebElement lawyer,
        By[] locators,
        String fieldName,
        String attributeName,
        Function<String, LawyerExceptions> exceptionSupplier
    ) throws LawyerExceptions;
}
```

**Usage Example:**
```java
// In a scraper class
By[] nameLocators = { By.className("lawyer-name"), By.cssSelector("a") };
String name = extractor.extractLawyerText(
    lawyer,
    nameLocators,
    "NAME",
    LawyerExceptions::nameException
);
```

### 5.2 SiteUtils.java

Helper methods for common scraping tasks.

| Method | Purpose | Usage |
|--------|---------|-------|
| `iterateOverBy(By[], WebElement)` | Navigate through nested locators | `siteUtl.iterateOverBy(byArray, lawyer)` |
| `isValidPosition(String, String[])` | Check if role is valid | `siteUtl.isValidPosition(role, validRoles)` |
| `filterLawyersInPage(List, By[], boolean, String[])` | Pre-filter lawyers by role | Returns only valid lawyers |
| `getContentFromTag(WebElement)` | Extract text from HTML tag | `siteUtl.getContentFromTag(element)` |
| `titleString(String)` | Title-case a string | `"john doe"` → `"John Doe"` |
| `getCountryBasedInOffice(Map, String, String)` | Map office to country | `siteUtl.getCountryBasedInOffice(map, "london", "England")` |
| `getCountryBasedInOfficeByPhone(Map, String, String)` | Map phone prefix to country | For firms using phone-based location |

### 5.3 TreatLawyerParams.java

Data cleaning and normalization utilities.

| Method | Purpose | Example |
|--------|---------|---------|
| `treatName(String)` | Clean and capitalize name | `"dr. john DOE, llm"` → `"John Doe"` |
| `treatEmail(String)` | Normalize email | `"mailto:John@Firm.com"` → `"john@firm.com"` |
| `treatPhone(String)` | Clean phone number | `"+44 (0) 123-456"` → `"44123456"` |
| `treatRole(String)` | Normalize role title | `"Senior managing partner"` → `"Senior Partner"` |
| `treatPracticeArea(String)` | Clean practice area | Remove generic words |
| `treatSpecialism(String)` | Determine specialism | `"Partner"` → `"Legal"` |
| `removeAccents(String)` | Remove diacritics | `"José"` → `"Jose"` |
| `treatNameForEmail(String)` | Prepare name for email creation | Lowercase, no accents |
| `getNameFromEmail(String)` | Extract name from email | `"john.doe@firm.com"` → `"John Doe *****"` |

### 5.4 Validations.java

Centralized validation rules.

```java
public class Validations {
    /**
     * Run all validations on a lawyer
     * @throws ValidationExceptions if any validation fails
     */
    public static void makeValidations(
        Lawyer lawyer,
        Set<String> lastCountries,
        String emailsOfMonthPath,
        String emailsToAvoidPath
    ) throws ValidationExceptions;

    // Individual checks
    public static boolean isACountryToAvoid(String country);
    public static boolean isATemporaryCountryToAvoid(String country);
    public static boolean isAFirmToAVoid(String firmName);
}
```

**Validation Flow:**
1. Check if country is permanently avoided
2. Check if country is temporarily avoided (disabled continent)
3. Check if email is on the avoid list
4. Check if email already collected this month
5. Check if country already collected for this firm (same session)

### 5.5 ContinentConfig.java

Read and manage continent configuration.

```java
public class ContinentConfig {
    public static boolean isContinentEnabled(String continentName);
    public static Set<String> getEnabledContinents();
    public static Set<String> getDisabledContinents();
}
```

### 5.6 EmailDuplicateChecker.java

Verifica duplicatas globais de e-mail consultando uma planilha Google Sheets. Singleton — requer `login()` antes do primeiro uso.

```java
public class EmailDuplicateChecker {
    public static EmailDuplicateChecker getINSTANCE();

    // Autenticação via Google OAuth (deve ser chamado antes de getINSTANCE())
    public static void login();

    // Retorna true se o e-mail já está registrado na planilha remota
    public boolean isEmailRegistered(String email);
}
```

### 5.7 NoSleep.java

Impede a suspensão do sistema durante a execução. Usa `caffeinate` no macOS e JNA (`Kernel32`) no Windows.

```java
public class NoSleep {
    public static void preventSleep();
    public static void allowSleep();   // libera o bloqueio ao finalizar
}
```

### 5.8 Stopwatch.java

Temporizador simples para medir o tempo de execução por firma.

```java
public class Stopwatch {
    public void start();
    public void stop();
    public String format();   // retorna "Xm Ys" com o tempo decorrido
}
```

### 5.9 FirmsExhausted.java

Rastreia firmas que esgotaram todos os contatos disponíveis, persistindo em `data/exhaustedFirms.txt` para excluí-las de execuções futuras.

```java
public class FirmsExhausted {
    public static boolean isExhausted(String firmName);
    public static void markExhausted(String firmName);
}
```

### 5.10 ErrorLogger.java

Comprehensive error tracking system.

```java
public class ErrorLogger {
    // Mark firm as being processed
    public void startFirm(String firmName);

    // Record successful lawyer registration
    public void recordLawyerRegistered(String firmName);

    // Log an error (auto-categorized)
    public void log(String firmName, Exception e, boolean showLogs);
    public void log(String firmName, Exception e, boolean showLogs, String context);

    // Write all logs to file
    public void flushAllLogs();
}
```

**Error Categories (auto-detected):**
- `linkException` - Failed to extract link
- `nameException` - Failed to extract name
- `emailException` - Failed to extract email
- `accessPageError` - Failed to access page
- `getLawyersInPageError` - Failed to get lawyers list
- `openNewTabError` - Failed to open profile tab
- `getSocialsError` - Failed to extract contact info

### 5.11 VCard.java

Parses `.vcf` (VCard) files downloaded from law firm websites and extracts email and phone.
Injectable as a field in any `ByPage` or `ByNewPage` firm class.

Because different sites produce VCards with different field names, the constructor requires
explicit **regex patterns** for email and phone. Each pattern must contain one capturing
group that isolates the value.

#### Default patterns

```java
// Matches: EMAIL:x@y.com  /  EMAIL;PREF;INTERNET:x@y.com
VCard.DEFAULT_EMAIL_PATTERN = "EMAIL[^:\\r\\n]*:([^\\r\\n]+)"

// Matches: TEL:+55 11 1234  /  TEL;WORK;VOICE:+55 11 1234
VCard.DEFAULT_PHONE_PATTERN = "TEL[^:\\r\\n]*:([^\\r\\n]+)"
```

#### API

| Method | Description |
|--------|-------------|
| `new VCard(emailPattern, phonePattern)` | Creates instance with custom patterns |
| `VCard.withDefaultPatterns()` | Factory using the standard VCard 2.1 / 3.0 patterns |
| `getSocials(String vcardUrl)` | Downloads VCF via plain HTTP GET, returns `String[]{ email, phone }` |
| `getSocials(WebDriver driver, String vcardUrl)` | Downloads VCF forwarding the active Selenium session cookies — use for authenticated endpoints |
| `parse(String vcfContent)` | Parses already-loaded VCF text — use when content comes from a page element or other source |

> Extracted values are automatically cleaned via `TreatLawyerParams.treatEmail()` and
> `TreatLawyerParams.treatPhone()` before being returned.

#### Usage inside a firm class

```java
// ── declare as a field ──────────────────────────────────────────────────────

// Standard VCard 2.1 / 3.0 (covers most firms)
private final VCard vCard = VCard.withDefaultPatterns();

// Custom patterns when the site uses non-standard field names
private final VCard vCard = new VCard(
    "X-EMAIL[^:\\r\\n]*:([^\\r\\n]+)",
    "X-TEL[^:\\r\\n]*:([^\\r\\n]+)"
);

// ── inside getLawyer() ──────────────────────────────────────────────────────

// 1. Find the VCard download link element and get its URL
String vcardUrl = lawyer.findElement(By.cssSelector("a.vcard-download"))
                        .getAttribute("href");

// 2a. Public VCF endpoint (no session needed)
String[] socials = vCard.getSocials(vcardUrl);

// 2b. Protected endpoint (requires active browser session cookies)
String[] socials = vCard.getSocials(driver, vcardUrl);

// 2c. Content already available as text (e.g. hidden element, API response)
String vcfText = driver.findElement(By.id("vcard-data")).getAttribute("textContent");
String[] socials = vCard.parse(vcfText);

// 3. Use the result
String email = socials[0];
String phone  = socials[1];
```

---

## 6. Configuration System

### 6.1 CONFIG.java

Central configuration constants.

```java
public class CONFIG {
    // File paths
    public static String SHEET_FILE = "src/main/resources/baseFiles/excel/Sheet.xlsx";
    public static String REPORTS_FILE = "src/main/resources/baseFiles/excel/Reports.xlsx";
    public static String FILTERED_ACTIVE_CONTACTS_FILE = "...filteredCollectedContacts.xlsx";
    public static String LAST_FIRM_REGISTERED_FILE = "...lastRowRegisteredInContacts.txt";

    public static String EMAILS_MONTH_FOLDER_FILE = "data/sites/";
    public static String EMAILS_TO_AVOID_FOLDER_FILE = "data/_toAvoid/";

    public static String BY_PAGE_FILE = "byPage/";
    public static String BY_NEW_PAGE_FILE = "byNewPage/";
    public static String MONTH_FILE = "data/monthFirms.txt";

    // Limits
    public static int TOTAL_LAWYERS_TO_GET = 350;  // Stop web scraping when reached
    public static int LAWYERS_IN_SHEET = 250;      // Reference value
    public static int LAWYERS_IN_FILTER = 100;     // Limit for filtered contacts

    // Timing
    public static int TIMEOUT_MINUTES = 4;         // Max time per firm
}
```

---

## 7. Continent Configuration

### 7.1 Central Configuration File

**Location:** `src/main/resources/baseFiles/json/continentsConfig.json`

```json
{
  "Africa":   { "enabled": true,  "weight": 1 },
  "Asia":     { "enabled": true,  "weight": 3 },
  "Europe":   { "enabled": true,  "weight": 1 },
  "Americas": { "enabled": false, "weight": 1 },
  "Oceania":  { "enabled": true,  "weight": 2 }
}
```

### 7.2 Configuration Effects

| Continent State | Firms | Countries |
|-----------------|-------|-----------|
| `enabled: true` | **Built** and included in scraping | **NOT avoided** in validation |
| `enabled: false` | **NOT built**, excluded from scraping | **Avoided** in validation |

### 7.3 Affected Components

1. **Builder** (`FirmsBuilder.java`)
   - Only include firms from enabled continents
   - `MUNDIAL` (global) firms always included

2. **Validations** (`Validations.java`)
   - Countries from disabled continents are avoided
   - Uses `countriesToAvoidTemporary.json` for country-continent mapping

### 7.4 Firm Organization in Builders

`FirmsBuilder` usa **reflection** (biblioteca Reflections) para descobrir automaticamente todas as classes concretas de `Site` dentro de cada pacote de continente. Não há arrays estáticos — basta colocar a classe no pacote correto.

```java
// FirmsBuilder.java — discovery automático via reflection.
// Para registrar uma nova firma, coloque-a no pacote correto
// (org.example.src.sites.<continente>) — nenhuma alteração aqui é necessária.

private static Site[] scanContinent(String continent) {
    Reflections reflections = new Reflections(
        "org.example.src.sites." + continent
    );
    return reflections.getSubTypesOf(Site.class).stream()
        .filter(c -> !Modifier.isAbstract(c.getModifiers()))
        .map(c -> (Site) c.getDeclaredConstructor().newInstance())
        .toArray(Site[]::new);
}

public static Site[] getAfrica()   { return scanContinent("africa"); }
public static Site[] getAsia()     { return scanContinent("asia"); }
public static Site[] getEurope()   { return scanContinent("europe"); }
public static Site[] getAmericas() { return scanContinent("americas"); }
public static Site[] getOceania()  { return scanContinent("oceania"); }
public static Site[] getMundial()  { return scanContinent("mundial"); }

// Build method respects configuration
public static Site[] build() {
    List<Site> sites = new ArrayList<>();

    if (ContinentConfig.isContinentEnabled("Africa"))   sites.addAll(Arrays.asList(getAfrica()));
    if (ContinentConfig.isContinentEnabled("Asia"))     sites.addAll(Arrays.asList(getAsia()));
    if (ContinentConfig.isContinentEnabled("Europe"))   sites.addAll(Arrays.asList(getEurope()));
    if (ContinentConfig.isContinentEnabled("Americas")) sites.addAll(Arrays.asList(getAmericas()));
    if (ContinentConfig.isContinentEnabled("Oceania"))  sites.addAll(Arrays.asList(getOceania()));
    sites.addAll(Arrays.asList(getMundial())); // Always add

    return sites.toArray(new Site[0]);
}
```

### 7.5 Available Continents

| Continent | Package | Config key | Notes |
|-----------|---------|------------|-------|
| Africa | `africa` | `"Africa"` | |
| Asia | `asia` | `"Asia"` | |
| Europe | `europe` | `"Europe"` | |
| Americas | `americas` | `"Americas"` | North + Central + South America no mesmo pacote |
| Oceania | `oceania` | `"Oceania"` | |
| Mundial (Global) | `mundial` | *(sempre ativo)* | Sempre incluído independente da config |

NOTE: If the country registered is from more than one continent, like Turkey (Asia and Europe), guarantee that the continent showed is EUROPE

---

## 8. Creating New Scrapers

### 8.1 Step-by-Step Guide

#### Step 1: Analyze the Target Website

1. **Determine the strategy:**
   - Is all data visible on listing pages? → Use `ByPage`
   - Need to click into each profile? → Use `ByNewPage`

2. **Identify HTML selectors for:**
   - Lawyer container element
   - Name element
   - Role/title element
   - Email/phone elements
   - Country/office element (if applicable)
   - Practice area element (if applicable)

3. **Identify the continent** the firm belongs to

#### Step 2: Create the Class File

**Localização para desenvolvimento/teste:**
- `core/src/main/java/org/example/src/sites/to_test/{continent}/`

**Localização para produção** (após testes aprovados):
- `firms/src/main/java/org/example/src/sites/{continent}/`

O fluxo recomendado é desenvolver e testar em `to_test/`, depois mover com `move_firms.py`.

#### Step 3: Implement Required Methods

### 8.2 ByPage Template

```java
package org.example.src.sites.to_test.americas; // ajuste o continente conforme necessário

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class NewFirmName extends ByPage {

    // Office to country mapping (for multinational firms)
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
        entry("london", "England"),
        entry("paris", "France"),
        entry("new york", "USA")
    );


    public NewFirmName() {
        super(
        "Firm Display Name",    // name (used in reports)
        "https://firm.com/people", // starting URL
        3,                      // total pages to scrape
        2                       // max lawyers to collect
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        // Navigate to the page
        String url = this.link + "?page=" + (index + 1);
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Accept cookies if needed (first page only)
        if (index == 0) {
            MyDriver.clickOnAddBtn(By.id("cookie-accept-button"));
        }

        // Scroll to load all content if needed
        MyDriver.rollDown(5, 0.5);
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".lawyer-card")
                )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.cssSelector("a.profile-link") };
        return extractor.extractLawyerAttribute(
            lawyer, byArray, "LINK", "href",
            LawyerExceptions::linkException
        );
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.cssSelector("h3.lawyer-name") };
        return extractor.extractLawyerText(
            lawyer, byArray, "NAME",
            LawyerExceptions::nameException
        );
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.cssSelector("span.lawyer-title") };
        return extractor.extractLawyerText(
            lawyer, byArray, "ROLE",
            LawyerExceptions::roleException
        );
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = { By.cssSelector("span.office") };
        String office = extractor.extractLawyerText(
            lawyer, byArray, "COUNTRY",
            LawyerExceptions::countryException
        );
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, office, "Not Found");
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> links = lawyer.findElements(By.cssSelector("a.contact-link"));
            return super.getSocials(links, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
            "link", this.getLink(lawyer),
            "name", this.getName(lawyer),
            "role", this.getRole(lawyer),
            "firm", this.name,
            "country", this.getCountry(lawyer),
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
    }
}
```

### 8.3 ByNewPage Template

```java
package org.example.src.sites.to_test.americas; // ajuste o continente conforme necessário

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NewFirmName extends ByNewPage {

    public NewFirmName() {
        super(
        "Firm Display Name",
        "https://firm.com/our-people",
        1,    // total pages
        2     // max lawyers
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Accept cookies
        MyDriver.clickOnAddBtn(By.id("accept-cookies"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("a.lawyer-profile-link")
                )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String profileUrl = lawyer.getAttribute("href");
        MyDriver.openNewTab(profileUrl);
        return null;
    }


    private String getName(WebElement profileDiv) throws LawyerExceptions {
        By[] byArray = { By.cssSelector("h1.lawyer-name") };
        return extractor.extractLawyerText(
            profileDiv, byArray, "NAME",
            LawyerExceptions::nameException
        );
    }


    private String getRole(WebElement profileDiv) throws LawyerExceptions {
        By[] byArray = { By.cssSelector("p.job-title") };
        return extractor.extractLawyerText(
            profileDiv, byArray, "ROLE",
            LawyerExceptions::roleException
        );
    }


    private String[] getSocials(WebElement profileDiv) {
        try {
            List<WebElement> socials = profileDiv.findElements(By.cssSelector("a.contact"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }


    // ── VCard alternative: when contact is only available via .vcf download ─
    private final VCard vCard = VCard.withDefaultPatterns();

    private String[] getSocialsFromVCard(WebElement profileDiv) {
        try {
            String vcardUrl = profileDiv
                .findElement(By.cssSelector("a[href$='.vcf']"))
                .getAttribute("href");
            return vCard.getSocials(driver, vcardUrl);   // use driver overload if login is required
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        // IMPORTANT: Open new tab first
        this.openNewTab(lawyer);

        // Find the main content div on the profile page
        WebElement profileDiv = driver.findElement(By.cssSelector(".profile-content"));

        String[] socials = this.getSocials(profileDiv);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(profileDiv),
            "role", this.getRole(profileDiv),
            "firm", this.name,
            "country", "England",  // or extract from page
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "xxxxxx" : socials[1]
        );
        // Tab is automatically closed by ByNewPage.searchForLawyers()
    }
}
```

### 8.4 Register the New Scraper

**Nenhuma alteração manual no `FirmsBuilder` é necessária.** O builder descobre todas as firmas via reflection escaneando os pacotes de continente. Basta mover o arquivo `.java` para o pacote de produção correto:

```
firms/src/main/java/org/example/src/sites/{continent}/NewFirmName.java
```

Use o script `move_firms.py` para automatizar essa movimentação de `to_test` para produção.

### 8.5 Testing

Run `Main.java` with the new scraper to verify:
- Pages load correctly
- Lawyers are found
- Data is extracted properly
- No exceptions thrown

---

## 9. Data Flow & Execution

### 9.1 Main Execution Flow

```
┌─────────────────────────────────────────────────────┐
│                    Main.main()                       │
├─────────────────────────────────────────────────────┤
│ 1. NoSleep.preventSleep()                           │
│ 2. EmailDuplicateChecker.login()                    │
│ 3. performCompleteSearch()                          │
│    └─ getRegisteredContacts()                       │
│    └─ searchLawyersInWeb(0)         [phase 1]       │
│ 4. searchLawyersInWeb(totalLawyers) [phase 2]       │
│ 5. finally:                                         │
│    - ErrorLogger.flushAllLogs()                     │
│    - Sheet.sortRows()  ← sort by D→J→E→F→C         │
│    - Close resources                                 │
└─────────────────────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│        searchLawyersInWeb(alreadyCollected)          │
├─────────────────────────────────────────────────────┤
│ 1. CompletedFirms.constructFirms()                  │
│    - Get firms from enabled continents               │
│    - Exclude firms processed this month              │
│    - Shuffle list                                    │
│                                                      │
│ 2. For each site:                                    │
│    - Check if target lawyers reached                 │
│    - Execute with timeout (4 min)                    │
│    - site.searchForLawyers()                        │
│    - Track lawyers registered                        │
│    - Create report row                               │
│                                                      │
│ 3. Stop when TOTAL_LAWYERS_TO_GET reached           │
└─────────────────────────────────────────────────────┘
```

### 9.2 Single Site Scraping Flow

```
┌─────────────────────────────────────────────────────┐
│           site.searchForLawyers()                    │
├─────────────────────────────────────────────────────┤
│ 1. errorLogger.startFirm(name)                      │
│                                                      │
│ 2. For each page:                                    │
│    a. accessPage(pageIndex)                          │
│    b. getLawyersInPage()                            │
│                                                      │
│    c. For each lawyer element:                       │
│       - getLawyer(element) → Map<String,String>     │
│       - registerValidLawyer(map)                    │
│         └── Validations.makeValidations()           │
│         └── addLawyer() if valid                    │
│             └── Sheet.addLawyer()                   │
│             └── EmailOfMonth.register()             │
│             └── errorLogger.recordLawyerRegistered()│
│                                                      │
│ 3. Stop if maxLawyersForSite reached                │
└─────────────────────────────────────────────────────┘
```

### 9.3 Validation Pipeline

```
Lawyer Data
    │
    ▼
┌─────────────────────────────────┐
│ Is email empty?                 │──Yes──▶ REJECT
└─────────────────────────────────┘
    │ No
    ▼
┌─────────────────────────────────┐
│ Is country permanently avoided? │──Yes──▶ REJECT
│ (countriesToAvoidPermanent.json)│
└─────────────────────────────────┘
    │ No
    ▼
┌─────────────────────────────────┐
│ Is country temporarily avoided? │──Yes──▶ REJECT
│ (continent disabled)            │
└─────────────────────────────────┘
    │ No
    ▼
┌─────────────────────────────────┐
│ Is email on avoid list?         │──Yes──▶ REJECT
│ (data/_toAvoid/)                │
└─────────────────────────────────┘
    │ No
    ▼
┌─────────────────────────────────┐
│ Email collected this month?     │──Yes──▶ REJECT
│ (data/sites/)                   │
└─────────────────────────────────┘
    │ No
    ▼
┌─────────────────────────────────┐
│ Country already in this firm?   │──Yes──▶ REJECT
│ (same session)                  │
└─────────────────────────────────┘
    │ No
    ▼
   ACCEPT → Register to Sheet.xlsx
```

---

## 10. Error Logging System

### 10.1 Overview

The `ErrorLogger` tracks errors during scraping for maintenance purposes. It generates a comprehensive report at the end of execution.

### 10.2 How to Use

```java
// At start of firm processing
errorLogger.startFirm(this.name);

// When a lawyer is successfully registered
errorLogger.recordLawyerRegistered(this.name);

// When an error occurs
errorLogger.log(this.name, exception, showLogs);
errorLogger.log(this.name, exception, showLogs, "Error context");

// At end of execution (in finally block)
errorLogger.flushAllLogs();
```

### 10.3 Log File Output Format

**Location:** `log.txt`

```
================================================================================
                           ERROR LOG - 2026-02-08 10:30:00
================================================================================

--------------------------------------------------------------------------------
                               ERRORS BY FIRM
--------------------------------------------------------------------------------

Class WhiteAndCase [5 lawyers registered, 12 errors]
   - getSocialsError               occurred 7 times
   - emailException                occurred 5 times

Class BakerMcKenzie [3 lawyers registered, 8 errors]
   - accessPageError               occurred 4 times
   - getLawyersInPageError         occurred 4 times

--------------------------------------------------------------------------------
                    [WARNING] FIRMS WITH ZERO LAWYERS REGISTERED
--------------------------------------------------------------------------------

Total: 2 firms need attention

   [X] DLAPiper (3 errors)
   [X] CliffordChance (no errors logged - possible timeout or page issue)

--------------------------------------------------------------------------------
                               ERROR SUMMARY
--------------------------------------------------------------------------------

Total errors: 23

Most common errors:
   getSocialsError                     11  ( 47.8%)
   accessPageError                      8  ( 34.8%)
   emailException                       4  ( 17.4%)

================================================================================
Firms processed: 15 | Firms with errors: 4 | Firms with 0 lawyers: 2
================================================================================
```

### 10.4 Error Categories

Errors are automatically categorized based on:

1. **LawyerExceptions message content:**
   - `LINK` → `linkException`
   - `NAME` → `nameException`
   - `EMAIL` → `emailException`
   - `PHONE` → `phoneException`
   - `ROLE` → `roleException`
   - `COUNTRY` → `countryException`
   - `PRACTICE` → `practiceAreaException`

2. **Stack trace method names:**
   - `accessPage` → `accessPageError`
   - `getLawyersInPage` → `getLawyersInPageError`
   - `openNewTab` → `openNewTabError`
   - `getSocials` → `getSocialsError`
   - etc.

---

## 11. Code Generation Guidelines

When generating scraper classes for AI/automated tools:

### 11.1 Response Structure

1. **`touch` Command** (create files):
   ```bash
   touch core/src/main/java/org/example/src/sites/to_test/americas/Firm1.java core/src/main/java/org/example/src/sites/to_test/americas/Firm2.java
   ```

2. **Class Generation** (for each firm):
   - Title with firm name
   - Complete Java class code

3. **Continent placement** (at end):
   ```text
   // Indicate which continent package each class should go into:
   // europe → firms/.../sites/europe/FirmA.java
   // asia   → firms/.../sites/asia/FirmC.java
   // americas → firms/.../sites/americas/FirmE.java
   ```

### 11.2 Formatting Rules

- **Package**: `org.example.src.sites.to_test`
- **Parameter Names**: `WebElement` parameters named `lawyer`
- **`super()` Indentation**: Arguments one level less indented
- **Default Phone**: `"xxxxxx"` for single-country firms

### 11.3 Email Construction from Name

When email must be constructed from name:

```java
// In getLawyer method
String name = this.getName(lawyer);
String[] socials = this.getSocials(lawyer, name);

// In getSocials method
private String[] getSocials(WebElement lawyer, String name) {
    name = TreatLawyerParams.treatNameForEmail(name);
    String[] parts = name.split(" ");
    String email = parts[0].charAt(0) + parts[parts.length - 1] + "@firm.com";
    return new String[]{ email, "" };
}
```

---

## 12. Configuration Files Reference

### 12.1 continentsConfig.json

Controls which continents are active.

```json
{
  "Africa":   { "enabled": true,  "weight": 1 },
  "Asia":     { "enabled": true,  "weight": 3 },
  "Europe":   { "enabled": true,  "weight": 1 },
  "Americas": { "enabled": false, "weight": 1 },
  "Oceania":  { "enabled": true,  "weight": 2 }
}
```

### 12.2 countriesToAvoidPermanent.json

Countries always avoided (regardless of config).

```json
{
  "countries": ["North Korea", "Iran", "Syria", ...]
}
```

### 12.3 countriesToAvoidTemporary.json

Countries by continent (avoided when continent disabled).

```json
{
  "Africa": ["South Africa", "Nigeria", ...],
  "Asia": ["China", "Japan", ...],
  "Europe": ["England", "Germany", ...],
  ...
}
```

### 12.4 firmsToAvoid.json

Specific firms to skip.

```json
{
  "firms": ["FirmName1", "FirmName2"]
}
```

### 12.5 countryAliases.json

Mapeamento de aliases/variações de nomes de países para o nome canônico usado no sistema.

```json
{
  "UK": "England",
  "United Kingdom": "England",
  "United Arab Emirates": "the UAE",
  ...
}
```

### 12.6 practiceAreas.json

Lista de áreas de prática válidas para normalização do campo `practiceArea`.

### 12.7 systemSnapshot.json

Snapshot do estado do sistema gerado por `CompletedFirms` ao encerrar. Contém métricas de firmas disponíveis, esgotadas e do mês para diagnóstico rápido.

---

## Quick Reference Card

### Constructor Parameters

| Parameter | Description |
|-----------|-------------|
| `name` | Display name for reports |
| `link` | Starting URL |
| `totalPages` | Number of pages to scrape |
| `maxLawyersForSite` | Stop after this many lawyers |

### Return Map Keys

```java
Map.of(
    "link",         "https://...",
    "name",         "John Doe",
    "role",         "Partner",
    "firm",         "Firm Name",
    "country",      "England",
    "practice_area", "Corporate",
    "email",        "john@firm.com",
    "phone",        "123456789"
)
```

### Extractor Methods

```java
extractor.extractLawyerText(element, byArray, "FIELD", LawyerExceptions::fieldException)
extractor.extractLawyerAttribute(element, byArray, "FIELD", "href", LawyerExceptions::fieldException)
```

### MyDriver Methods

```java
MyDriver.waitForPageToLoad();
MyDriver.rollDown(times, sleepSeconds);
MyDriver.clickOnElement(By.id("..."));
MyDriver.clickOnAddBtn(By.id("cookie-btn"));
MyDriver.openNewTab("https://...");
MyDriver.closeCurrentTab();
```

### VCard Methods

```java
// Instantiation
VCard vCard = VCard.withDefaultPatterns();                   // standard VCard 2.1/3.0
VCard vCard = new VCard("EMAIL[^:\\r\\n]*:([^\\r\\n]+)",     // custom email pattern
                         "TEL[^:\\r\\n]*:([^\\r\\n]+)");     // custom phone pattern

// Extraction — all return String[]{ email, phone }
vCard.getSocials(vcardUrl);               // plain HTTP download
vCard.getSocials(driver, vcardUrl);       // HTTP download + Selenium session cookies
vCard.parse(vcfText);                     // parse already-loaded VCF text
```

---

*Document generated: March 2026*
